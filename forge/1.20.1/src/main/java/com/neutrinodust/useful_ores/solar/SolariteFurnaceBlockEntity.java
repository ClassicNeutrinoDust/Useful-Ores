package com.neutrinodust.useful_ores.solar;

import com.neutrinodust.useful_ores.util.NbtCompat;

import net.minecraft.nbt.CompoundTag;
import com.neutrinodust.useful_ores.init.ModBlockEntities;
import com.neutrinodust.useful_ores.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.minecraftforge.items.wrapper.InvWrapper;

public class SolariteFurnaceBlockEntity extends BlockEntity implements Container, WorldlyContainer, MenuProvider,
        software.bernie.geckolib.core.animatable.GeoAnimatable {

    public static final int SLOT_BATTERY = 0;
    public static final int SLOT_INPUT = 1;
    public static final int SLOT_OUTPUT = 2;

    public static final int SLOT_DEAD_BATTERY = 3;
    public static final int DEAD_BATTERY_STACK_LIMIT = 16;

    private static final int[] SLOTS_TOP = new int[]{SLOT_INPUT};
    private static final int[] SLOTS_SIDE = new int[]{SLOT_BATTERY};
    private static final int[] SLOTS_BOTTOM = new int[]{SLOT_OUTPUT, SLOT_DEAD_BATTERY};

    private static final float SPEED_MULTIPLIER = 5.0F;

    public static final int MODE_NONE = 0;
    public static final int MODE_SUN = 1;
    public static final int MODE_BATTERY = 2;

    private NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);
    @SuppressWarnings("unchecked")
    private final LazyOptional<IItemHandler>[] itemHandlers = new LazyOptional[Direction.values().length + 1];

    private int cookTime;
    private int cookTimeTotal;
    private int sourceMode = MODE_NONE;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> cookTime;
                case 1 -> cookTimeTotal;
                case 2 -> sourceMode;
                case 3 -> batteryEnergyPercent();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> cookTime = value;
                case 1 -> cookTimeTotal = value;
                case 2 -> sourceMode = value;
                default -> {}
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public SolariteFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SOLARITE_FURNACE.get(), pos, state);
    }

    @Override
    public AABB getRenderBoundingBox() {
        BlockPos pos = getBlockPos();
        return new AABB(pos.getX(), pos.getY(), pos.getZ(),
                pos.getX() + 2.0D, pos.getY() + 2.0D, pos.getZ() + 2.0D).inflate(0.01D);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction side) {
        if (capability == ForgeCapabilities.ITEM_HANDLER) {
            int index = side == null ? Direction.values().length : side.ordinal();
            LazyOptional<IItemHandler> handler = itemHandlers[index];
            if (handler == null || !handler.isPresent()) {
                handler = LazyOptional.of(() -> side == null ? new InvWrapper(this) : new SidedInvWrapper(this, side));
                itemHandlers[index] = handler;
            }
            return handler.cast();
        }
        return super.getCapability(capability, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        for (int i = 0; i < itemHandlers.length; i++) {
            if (itemHandlers[i] != null) itemHandlers[i].invalidate();
            itemHandlers[i] = null;
        }
    }

    private int batteryEnergyPercent() {
        ItemStack battery = items.get(SLOT_BATTERY);
        if (battery.isEmpty() || !battery.is(ModItems.SOLAR_BATTERY.get())) return 0;
        return Math.round(100.0F * SolarBatteryItem.getEnergy(battery) / SolarBatteryItem.MAX_ENERGY);
    }

    public int getCookProgressScaled(int scale) {
        return cookTimeTotal <= 0 ? 0 : Math.min(scale, cookTime * scale / cookTimeTotal);
    }

    public int getSourceMode() {
        return sourceMode;
    }

    public static void tick(net.minecraft.world.level.Level level, BlockPos pos, BlockState state, SolariteFurnaceBlockEntity be) {
        if (level.isClientSide) return;

        be.retireDeadBatteryIfDrained();

        boolean isDaytime = (level.getDayTime() % 24000L) < 12000L;

        boolean sunlight = isDaytime && !level.isRaining() && level.canSeeSky(pos.above(2));

        ItemStack battery = be.items.get(SLOT_BATTERY);
        boolean batteryHasCharge = !battery.isEmpty() && battery.is(ModItems.SOLAR_BATTERY.get())
                && SolarBatteryItem.getEnergy(battery) > 0;

        net.minecraft.server.level.ServerLevel serverLevel = (net.minecraft.server.level.ServerLevel) level;

        net.minecraft.world.SimpleContainer recipeInput =
                new net.minecraft.world.SimpleContainer(be.items.get(SLOT_INPUT).copy());
        Optional<BlastingRecipe> match = serverLevel.getRecipeManager().getRecipeFor(
                RecipeType.BLASTING, recipeInput, serverLevel);

        ItemStack recipeResult = match.map(recipe ->
                recipe.assemble(recipeInput, level.registryAccess())).orElse(ItemStack.EMPTY);
        boolean canCraft = match.isPresent() && be.canAcceptResult(recipeResult);

        boolean powered = sunlight || batteryHasCharge;
        boolean changed = false;

        if (powered && canCraft) {
            be.sourceMode = sunlight ? MODE_SUN : MODE_BATTERY;

            if (be.cookTimeTotal <= 0) {
                be.cookTimeTotal = Math.max(1, Math.round(match.get().getCookingTime() / SPEED_MULTIPLIER));
            }
            be.cookTime++;

            if (!sunlight) {
                int energy = SolarBatteryItem.getEnergy(battery);
                SolarBatteryItem.setEnergy(battery, energy - 1);
            }

            if (be.cookTime >= be.cookTimeTotal) {
                be.craft(recipeResult);
                be.cookTime = 0;
                be.cookTimeTotal = 0;
            }
            changed = true;
        } else {
            be.sourceMode = powered ? (sunlight ? MODE_SUN : MODE_BATTERY) : MODE_NONE;
            if (be.cookTime > 0) {

                be.cookTime = Math.max(0, be.cookTime - 2);
                changed = true;
            }
        }

        if (changed) be.setChanged();

        boolean lit = powered && canCraft;
        if (state.hasProperty(SolariteFurnaceBlock.LIT) && state.getValue(SolariteFurnaceBlock.LIT) != lit) {
            level.setBlock(pos, state.setValue(SolariteFurnaceBlock.LIT, lit), 3);
        }

        if (sunlight && level.getGameTime() % 5 == 0) {
            spawnSunPullParticles(serverLevel, pos);
        }

        be.pushDeadBatteriesToChest(serverLevel);
    }

    private static void spawnSunPullParticles(net.minecraft.server.level.ServerLevel serverLevel, BlockPos origin) {
        net.minecraft.util.RandomSource random = serverLevel.getRandom();
        double topY = origin.getY() + 2.0 + 1.2 + random.nextDouble() * 0.8;
        double px = origin.getX() + random.nextDouble() * 2.0;
        double pz = origin.getZ() + random.nextDouble() * 2.0;
        serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.END_ROD,
                px, topY, pz, 0, 0.0, -0.05, 0.0, 1.0);
    }

    private void retireDeadBatteryIfDrained() {
        ItemStack battery = items.get(SLOT_BATTERY);
        if (battery.isEmpty() || !battery.is(ModItems.SOLAR_BATTERY.get())) return;
        if (SolarBatteryItem.getEnergy(battery) > 0) return;

        items.set(SLOT_BATTERY, ItemStack.EMPTY);
        addDeadBattery();
        setChanged();
    }

    private void addDeadBattery() {
        ItemStack newDead = new ItemStack(ModItems.SOLAR_BATTERY.get());
        SolarBatteryItem.setEnergy(newDead, 0);

        ItemStack dead = items.get(SLOT_DEAD_BATTERY);
        if (dead.isEmpty()) {
            items.set(SLOT_DEAD_BATTERY, newDead);
            return;
        }
        if (dead.getCount() < DEAD_BATTERY_STACK_LIMIT && ItemStack.isSameItemSameTags(dead, newDead)) {
            dead.grow(1);
            return;
        }

        Container chest = findAdjacentChestContainer();
        if (chest != null && insertIntoContainer(chest, newDead)) {
            chest.setChanged();
            return;
        }
        if (level != null) {
            net.minecraft.world.level.block.Block.popResource(level, worldPosition, newDead);
        }
    }

    private void pushDeadBatteriesToChest(net.minecraft.server.level.ServerLevel serverLevel) {
        ItemStack dead = items.get(SLOT_DEAD_BATTERY);
        if (dead.isEmpty()) return;

        BlockPos[] linkCell = new BlockPos[1];
        net.minecraft.core.Direction[] linkDir = new net.minecraft.core.Direction[1];
        Container chest = findAdjacentChestContainer(linkCell, linkDir);
        if (chest == null) return;

        spawnConnectionEffect(serverLevel, linkCell[0], linkDir[0]);

        if (insertIntoContainer(chest, dead)) {
            items.set(SLOT_DEAD_BATTERY, dead.isEmpty() ? ItemStack.EMPTY : dead);
            chest.setChanged();
            setChanged();
        }
    }

    private static boolean insertIntoContainer(Container container, ItemStack stack) {
        boolean movedAny = false;
        for (int slot = 0; slot < container.getContainerSize() && !stack.isEmpty(); slot++) {
            ItemStack existing = container.getItem(slot);
            if (existing.isEmpty()) {
                int move = stack.getCount();
                ItemStack placed = stack.copy();
                container.setItem(slot, placed);
                stack.shrink(move);
                movedAny = true;
            } else if (ItemStack.isSameItemSameTags(existing, stack)) {
                int space = existing.getMaxStackSize() - existing.getCount();
                if (space > 0) {
                    int move = Math.min(space, stack.getCount());
                    existing.grow(move);
                    stack.shrink(move);
                    movedAny = true;
                }
            }
        }
        return movedAny;
    }

    private Container findAdjacentChestContainer(BlockPos[] outCell, net.minecraft.core.Direction[] outDir) {
        if (level == null) return null;
        BlockPos[] structure = SolariteFurnaceMultiblock.structurePositions(worldPosition);
        java.util.Set<BlockPos> structureSet = java.util.Set.of(structure);
        for (BlockPos cell : structure) {
            for (net.minecraft.core.Direction dir : net.minecraft.core.Direction.values()) {
                BlockPos neighbor = cell.relative(dir);
                if (structureSet.contains(neighbor)) continue;
                net.minecraft.world.level.block.entity.BlockEntity neighborBe = level.getBlockEntity(neighbor);
                if (neighborBe instanceof Container container && !(neighborBe instanceof SolariteFurnaceBlockEntity)) {
                    if (outCell != null) outCell[0] = cell;
                    if (outDir != null) outDir[0] = dir;
                    return container;
                }
            }
        }
        return null;
    }

    private Container findAdjacentChestContainer() {
        return findAdjacentChestContainer(null, null);
    }

    private static void spawnConnectionEffect(net.minecraft.server.level.ServerLevel serverLevel,
                                               BlockPos cell, net.minecraft.core.Direction dir) {
        double cx = cell.getX() + 0.5 + dir.getStepX() * 0.5;
        double cy = cell.getY() + 0.5 + dir.getStepY() * 0.5;
        double cz = cell.getZ() + 0.5 + dir.getStepZ() * 0.5;
        net.minecraft.core.particles.DustParticleOptions options =
                new net.minecraft.core.particles.DustParticleOptions(new org.joml.Vector3f(1.0F, 0.8F, 0.4F), 1.0F);
        serverLevel.sendParticles(options, cx, cy, cz, 2, 0.08, 0.08, 0.08, 0.0);
    }

    private boolean canAcceptResult(ItemStack result) {
        ItemStack out = items.get(SLOT_OUTPUT);
        if (out.isEmpty()) return true;
        if (!ItemStack.isSameItemSameTags(out, result)) return false;
        return out.getCount() + result.getCount() <= out.getMaxStackSize();
    }

    private void craft(ItemStack recipeResult) {
        ItemStack input = items.get(SLOT_INPUT);
        ItemStack result = recipeResult.copy();
        ItemStack out = items.get(SLOT_OUTPUT);

        if (out.isEmpty()) {
            items.set(SLOT_OUTPUT, result.copy());
        } else {
            out.grow(result.getCount());
        }
        input.shrink(1);
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = ContainerHelper.removeItem(items, slot, amount);
        if (!result.isEmpty()) setChanged();
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {

        boolean inputItemChanged = slot == SLOT_INPUT
                && !ItemStack.isSameItemSameTags(items.get(SLOT_INPUT), stack);

        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) stack.setCount(getMaxStackSize());

        if (slot == SLOT_BATTERY && stack.getCount() > 1) {
            ItemStack overflow = stack.copy();
            overflow.setCount(stack.getCount() - 1);
            stack.setCount(1);
            if (level != null) {
                net.minecraft.world.level.block.Block.popResource(level, worldPosition, overflow);
            }
        }

        if (slot == SLOT_DEAD_BATTERY && stack.getCount() > DEAD_BATTERY_STACK_LIMIT) {
            ItemStack overflow = stack.copy();
            overflow.setCount(stack.getCount() - DEAD_BATTERY_STACK_LIMIT);
            stack.setCount(DEAD_BATTERY_STACK_LIMIT);
            if (level != null) {
                net.minecraft.world.level.block.Block.popResource(level, worldPosition, overflow);
            }
        }

        if (inputItemChanged) {
            cookTime = 0;
            cookTimeTotal = 0;
        }
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        if (level == null || level.getBlockEntity(worldPosition) != this) return false;
        return player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void clearContent() {
        items.clear();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return switch (slot) {
            case SLOT_BATTERY -> stack.is(ModItems.SOLAR_BATTERY.get()) && items.get(SLOT_BATTERY).isEmpty();
            case SLOT_OUTPUT -> false;

            case SLOT_DEAD_BATTERY -> false;
            default -> true;
        };
    }

    @Override
    public int[] getSlotsForFace(net.minecraft.core.Direction side) {
        return switch (side) {
            case UP -> SLOTS_TOP;
            case DOWN -> SLOTS_BOTTOM;
            default -> SLOTS_SIDE;
        };
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, net.minecraft.core.Direction dir) {
        return canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, net.minecraft.core.Direction dir) {
        return slot == SLOT_OUTPUT || slot == SLOT_DEAD_BATTERY;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.useful_ores.solarite_furnace");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new SolariteFurnaceMenu(containerId, playerInventory, this, this.data,
                ContainerLevelAccess.create(this.level, this.worldPosition));
    }

    private final software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache geoCache =
            software.bernie.geckolib.util.GeckoLibUtil.createInstanceCache(this);

    @Override
    public software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    public void registerControllers(software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    protected void saveAdditional(CompoundTag output) {
        super.saveAdditional(output);
        output.putInt("CookTime", cookTime);
        output.putInt("CookTimeTotal", cookTimeTotal);
        ContainerHelper.saveAllItems(output, items);
    }

    @Override
    public void load(CompoundTag input) {
        super.load(input);
        cookTime = NbtCompat.getIntOr(input, "CookTime", 0);
        cookTimeTotal = NbtCompat.getIntOr(input, "CookTimeTotal", 0);
        items = NonNullList.withSize(4, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, items);
    }

    @Override
    public double getTick(Object object) {
        return this.level != null ? this.level.getGameTime() : 0.0D;
    }
}


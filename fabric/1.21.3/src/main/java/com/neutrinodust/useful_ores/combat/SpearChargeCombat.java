package com.neutrinodust.useful_ores.combat;

import com.neutrinodust.useful_ores.Constants;
import com.neutrinodust.useful_ores.client.spear.SpearAnimationProfile;
import com.neutrinodust.useful_ores.init.ModSpearSounds;
import com.neutrinodust.useful_ores.init.SpearTags;
import com.neutrinodust.useful_ores.network.SpearChargeFramePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.UUID;
import java.util.WeakHashMap;








public final class SpearChargeCombat {
    
    
    private static final double MIN_REACH = 0.4D;
    private static final double CREATIVE_MIN_REACH = 0.0D;
    private static final double MAX_REACH = 4.5D;
    private static final double CREATIVE_MAX_REACH = 6.5D;
    private static final double HITBOX_MARGIN = 0.25D;
    private static final long CONTACT_COOLDOWN = 10L;

    
    private static final Map<LivingEntity, UseState> ACTIVE_USES = new WeakHashMap<>();
    private static final boolean DEBUG_LOG = false;

    private SpearChargeCombat() {}

    




    public static void beginUse(LivingEntity attacker, ItemStack stack, InteractionHand hand, long now) {
        if (attacker.level().isClientSide()) return;
        synchronized (ACTIVE_USES) {
            ACTIVE_USES.put(attacker, new UseState(stack.copy(), hand, new HashMap<>()));
        }
    }

    public static void endUse(LivingEntity attacker, ItemStack stack) {
        if (attacker.level().isClientSide()) return;
        synchronized (ACTIVE_USES) {
            ACTIVE_USES.remove(attacker);
        }
    }

    



    public static void onUseTick(Level level, LivingEntity attacker, ItemStack stack, int remainingUseDuration) {
        if (!(level instanceof ServerLevel serverLevel)
                || !attacker.isAlive()
                || !stack.is(SpearTags.SPEARS)) {
            return;
        }

        InteractionHand hand = attacker instanceof Player player
                ? player.getUsedItemHand()
                : InteractionHand.MAIN_HAND;

        UseState state;
        synchronized (ACTIVE_USES) {
            state = ACTIVE_USES.get(attacker);
            if (state == null || state.stack.getItem() != stack.getItem() || state.hand != hand) {
                state = new UseState(stack.copy(), hand, new HashMap<>());
                ACTIVE_USES.put(attacker, state);
            }
        }

        
        
        
        
        
        int ticksUsed = Math.max(0, stack.getUseDuration(attacker) - remainingUseDuration);
        processUseTick(serverLevel, attacker, stack, state, ticksUsed);
    }

    



    public static void onClientChargeFrame(ServerPlayer attacker, SpearChargeFramePacket packet) {
        if (!attacker.isAlive() || !attacker.isUsingItem()) return;
        ItemStack stack = attacker.getUseItem();
        if (!stack.is(SpearTags.SPEARS)) return;

        synchronized (ACTIVE_USES) {
            UseState state = ACTIVE_USES.get(attacker);
            InteractionHand hand = attacker.getUsedItemHand();
            if (state == null || state.hand != hand || state.stack.getItem() != stack.getItem()) {
                state = new UseState(stack.copy(), hand, new HashMap<>());
                ACTIVE_USES.put(attacker, state);
            }
            state.clientVelocity20 = new Vec3(packet.velocityX20(), packet.velocityY20(), packet.velocityZ20());
            state.clientVelocityReceivedTick = attacker.level().getGameTime();
        }
    }

    private static UseState getState(LivingEntity attacker) {
        synchronized (ACTIVE_USES) {
            UseState state = ACTIVE_USES.get(attacker);
            if (state == null) {
                InteractionHand hand = attacker instanceof Player player ? player.getUsedItemHand() : InteractionHand.MAIN_HAND;
                state = new UseState(attacker.getUseItem().copy(), hand, new HashMap<>());
                ACTIVE_USES.put(attacker, state);
            }
            return state;
        }
    }

    
    public static boolean performJab(ServerPlayer attacker) {
        if (!attacker.isAlive() || !attacker.getMainHandItem().is(SpearTags.SPEARS)) return false;
        ItemStack stack = attacker.getMainHandItem();
        if (attacker.getCooldowns().isOnCooldown(stack)) return false;

        if (!(attacker.level() instanceof ServerLevel level)) return false;
        Vec3 view = attacker.getViewVector(1.0F).normalize();
        double maxReach = attacker.isCreative() ? CREATIVE_MAX_REACH : MAX_REACH;
        List<TargetHit> targets = findTargetsAlong(level, attacker, view, attacker.isCreative() ? CREATIVE_MIN_REACH : MIN_REACH, maxReach);
        float damage = (float) attacker.getAttributeValue(Attributes.ATTACK_DAMAGE);
        var source = level.damageSources().playerAttack(attacker);
        boolean hitAny = false;

        for (TargetHit targetHit : targets) {
            LivingEntity target = targetHit.entity();
            if (!target.isAlive() || target == attacker) continue;
            if (!target.hurtServer(level, source, damage)) continue;
            hitAny = true;

            Vec3 horizontal = new Vec3(view.x, 0.0D, view.z);
            if (horizontal.lengthSqr() > 1.0E-8D) {
                horizontal = horizontal.normalize();
                target.knockback(0.4D, -horizontal.x, -horizontal.z);
            }
            level.playSound(null, target.getX(), target.getY(), target.getZ(),
                    ModSpearSounds.SPEAR_ATTACK_HIT, SoundSource.PLAYERS, 1.0F, 1.0F);
        }

        int cooldownTicks = Math.max(1, Math.round(20.0F /
                (float) Math.max(0.01D, attacker.getAttributeValue(Attributes.ATTACK_SPEED))));
        attacker.getCooldowns().addCooldown(stack, cooldownTicks);
        Constants.LOG.info("[spear-super][jabHit] attacker={} hitAny={} targets={}",
                attacker.getName().getString(), hitAny, targets.size());
        return true;
    }

    private static void processUseTick(
            ServerLevel level,
            LivingEntity attacker,
            ItemStack stack,
            UseState state,
            int ticksUsed
    ) {
        SpearAnimationProfile profile = SpearAnimationProfile.forStack(stack);
        int delayTicks = Math.round(profile.chargeDelaySeconds() * 20.0F);
        int elapsed = ticksUsed - delayTicks;
        if (elapsed < 0) return;

        
        
        long now = level.getGameTime();

        Vec3 view = attacker.getViewVector(1.0F).normalize();

        
        
        
        Vec3 attackerMotion = state.clientVelocity20;
        if (attackerMotion == null || level.getGameTime() - state.clientVelocityReceivedTick > 3L) {
            attackerMotion = kineticMotion(attacker);
        }
        double attackerSpeed = attackerMotion.dot(view);

        boolean creative = attacker instanceof Player player && player.isCreative();
        double minReach = creative ? CREATIVE_MIN_REACH : MIN_REACH;
        double maxReach = creative ? CREATIVE_MAX_REACH : MAX_REACH;
        List<TargetHit> targets = findTargetsAlong(level, attacker, view, minReach, maxReach);

        if (DEBUG_LOG && attacker instanceof Player player && (ticksUsed % 5 == 0)) {
            Constants.LOG.info(
                    "[spear-kinetic] user={} used={}t elapsed={}t motion={} speedAlongView={} targets={}",
                    player.getName().getString(), ticksUsed, elapsed,
                    fmt(attackerMotion), String.format("%.3f", attackerSpeed), targets.size());
        }

        Set<UUID> currentContacts = new HashSet<>();
        for (TargetHit targetHit : targets) currentContacts.add(targetHit.entity().getUUID());

        
        
        
        for (Map.Entry<UUID, ContactState> entry : state.contacts.entrySet()) {
            if (!currentContacts.contains(entry.getKey())) {
                ContactState contact = entry.getValue();
                if (contact.hit && contact.touching) {
                    contact.touching = false;
                    contact.cooldownUntil = now + CONTACT_COOLDOWN;
                }
            }
        }

        for (TargetHit targetHit : targets) {
            LivingEntity target = targetHit.entity();
            if (!canKineticHit(attacker, target)) continue;

            ContactState contact = state.contacts.get(target.getUUID());
            if (contact != null && contact.hit && contact.cooldownUntil > now) {
                contact.touching = true;
                continue;
            }
            if (contact != null && contact.hit && contact.touching) {
                continue;
            }

            Vec3 targetMotion = kineticMotion(target);
            double targetSpeed = targetMotion.dot(view);
            double projectedRelativeSpeed = Math.max(0.0D, attackerSpeed - targetSpeed);

            
            
            
            
            
            
            
            
            
            double relativeMotionMagnitude = attackerMotion.subtract(targetMotion).length();

            
            
            
            double speedMultiplier = attacker instanceof Player ? 1.0D : 0.2D;

            boolean dismount = condition(
                    elapsed,
                    profile.maxDurationForDismountSeconds(),
                    attackerSpeed,
                    profile.minSpeedForDismount(),
                    target.isPassenger(),
                    speedMultiplier
            );
            boolean knockback = condition(
                    elapsed,
                    profile.maxDurationForChargeKnockbackSeconds(),
                    attackerSpeed,
                    profile.minSpeedForChargeKnockback(),
                    true,
                    speedMultiplier
            );
            boolean damage = conditionRelative(
                    elapsed,
                    profile.maxDurationForChargeDamageSeconds(),
                    relativeMotionMagnitude,
                    profile.minRelativeSpeedForChargeDamage(),
                    speedMultiplier
            );

            if (DEBUG_LOG && attacker instanceof Player) {
                Constants.LOG.info(
                        "[spear-kinetic] target={} hitDist={} attackerSpeed={} targetSpeed={} projectedRelative={} relativeMagnitude={} dismount={} knockback={} damage={}",
                        target.getName().getString(),
                        String.format("%.3f", targetHit.distance()),
                        String.format("%.3f", attackerSpeed),
                        String.format("%.3f", targetSpeed),
                        String.format("%.3f", projectedRelativeSpeed),
                        String.format("%.3f", relativeMotionMagnitude),
                        dismount, knockback, damage);
            }

            if (!(dismount || knockback || damage)) continue;

            boolean didDamage = false;
            if (damage) {
                float baseDamage = (float) attacker.getAttributeValue(Attributes.ATTACK_DAMAGE);
                float kineticDamage = (float) Math.floor(projectedRelativeSpeed * profile.chargeDamageMultiplier());
                float finalDamage = baseDamage + kineticDamage;
                var source = attacker instanceof Player player
                        ? level.damageSources().playerAttack(player)
                        : level.damageSources().mobAttack(attacker);
                didDamage = finalDamage > 0.0F && target.hurtServer(level, source, finalDamage);
            }

            if (knockback) {
                Vec3 horizontal = new Vec3(view.x, 0.0D, view.z);
                if (horizontal.lengthSqr() > 1.0E-8D) {
                    horizontal = horizontal.normalize();
                    target.knockback(0.5D, -horizontal.x, -horizontal.z);
                }
            }
            if (dismount) target.stopRiding();

            if (didDamage || knockback || dismount) {
                rememberHit(state, target, now);
                level.playSound(null, target.getX(), target.getY(), target.getZ(),
                        ModSpearSounds.SPEAR_HIT, SoundSource.PLAYERS, 1.0F, 1.0F);
                level.broadcastEntityEvent(attacker, (byte) 2);
                Constants.LOG.info("[spear-kinetic][HIT] attacker={} target={} damage={} relSpeedProjected={} relSpeedMagnitude={}",
                        attacker.getName().getString(), target.getName().getString(), didDamage, projectedRelativeSpeed, relativeMotionMagnitude);
            }
        }
    }

    private static Vec3 kineticMotion(Entity entity) {
        if (!(entity instanceof Player)
                && entity.isPassenger()
                && entity.getVehicle() != null) {
            entity = entity.getRootVehicle();
        }
        return entity.getDeltaMovement().scale(20.0D);
    }

    private static boolean condition(
            int elapsed,
            float maxDurationSeconds,
            double speed,
            float minSpeed,
            boolean targetRequirement,
            double speedMultiplier
    ) {
        return targetRequirement
                && elapsed <= Math.round(maxDurationSeconds * 20.0F)
                && speed >= minSpeed * speedMultiplier;
    }

    private static boolean conditionRelative(
            int elapsed,
            float maxDurationSeconds,
            double relativeSpeed,
            float minRelativeSpeed,
            double speedMultiplier
    ) {
        return elapsed <= Math.round(maxDurationSeconds * 20.0F)
                && relativeSpeed >= minRelativeSpeed * speedMultiplier;
    }

    




    private static List<TargetHit> findTargetsAlong(
            ServerLevel level,
            LivingEntity attacker,
            Vec3 view,
            double minReach,
            double maxReach
    ) {
        Vec3 eye = attacker.getEyePosition(1.0F);
        Vec3 start = eye.add(view.scale(minReach));
        Vec3 end = eye.add(view.scale(maxReach));

        var blockHit = level.clip(new ClipContext(
                start, end,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                attacker));
        Vec3 clippedEnd = blockHit.getType() == net.minecraft.world.phys.HitResult.Type.MISS
                ? end
                : blockHit.getLocation();

        AABB search = new AABB(start, clippedEnd).inflate(HITBOX_MARGIN + 0.001D);
        List<TargetHit> hits = new ArrayList<>();

        for (Entity entity : level.getEntities(attacker, search, e -> e instanceof LivingEntity
                && e != attacker
                && e.isAlive()
                && e.canBeHitByProjectile())) {
            LivingEntity target = (LivingEntity) entity;
            if (!canKineticHit(attacker, target)) continue;

            AABB targetBox = target.getBoundingBox().inflate(HITBOX_MARGIN);
            Double distanceAlong = segmentAabbDistance(start, clippedEnd, targetBox);
            if (distanceAlong == null) continue;

            Vec3 hitPoint = start.add(clippedEnd.subtract(start).scale(distanceAlong));
            double eyeDistanceSquared = hitPoint.distanceToSqr(eye);
            if (eyeDistanceSquared > maxReach * maxReach + 1.0E-6D) continue;
            hits.add(new TargetHit(target, Math.sqrt(eyeDistanceSquared)));
        }

        hits.sort(Comparator.comparingDouble(TargetHit::distance));
        return hits;
    }

    
    private static Double segmentAabbDistance(Vec3 start, Vec3 end, AABB box) {
        Vec3 d = end.subtract(start);
        double tMin = 0.0D;
        double tMax = 1.0D;

        double[] s = {start.x, start.y, start.z};
        double[] dir = {d.x, d.y, d.z};
        double[] min = {box.minX, box.minY, box.minZ};
        double[] max = {box.maxX, box.maxY, box.maxZ};

        for (int i = 0; i < 3; i++) {
            if (Math.abs(dir[i]) < 1.0E-10D) {
                if (s[i] < min[i] || s[i] > max[i]) return null;
                continue;
            }
            double inv = 1.0D / dir[i];
            double t1 = (min[i] - s[i]) * inv;
            double t2 = (max[i] - s[i]) * inv;
            if (t1 > t2) {
                double tmp = t1;
                t1 = t2;
                t2 = tmp;
            }
            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);
            if (tMin > tMax) return null;
        }
        return tMin;
    }

    private static boolean canKineticHit(LivingEntity attacker, LivingEntity target) {
        if (!target.isAlive() || target == attacker || !target.canBeHitByProjectile()) return false;
        if (attacker.getVehicle() != null && attacker.getVehicle() == target.getVehicle()) return false;
        if (target instanceof Player player && player.isInvulnerable()) return false;
        return true;
    }

    private static void rememberHit(UseState state, LivingEntity target, long now) {
        ContactState contact = state.contacts.computeIfAbsent(target.getUUID(), id -> new ContactState());
        contact.hit = true;
        contact.touching = true;
        contact.cooldownUntil = Long.MAX_VALUE;
    }

    private static String fmt(Vec3 v) {
        return String.format("(%.3f,%.3f,%.3f)", v.x, v.y, v.z);
    }

    private static final class ContactState {
        private boolean hit;
        private boolean touching;
        private long cooldownUntil = Long.MIN_VALUE;
    }

    private static final class UseState {
        private final ItemStack stack;
        private final InteractionHand hand;
        private final Map<UUID, ContactState> contacts;
        private Vec3 clientVelocity20;
        private long clientVelocityReceivedTick = Long.MIN_VALUE;

        private UseState(ItemStack stack, InteractionHand hand, Map<UUID, ContactState> contacts) {
            this.stack = stack;
            this.hand = hand;
            this.contacts = contacts;
        }
    }

    private record TargetHit(LivingEntity entity, double distance) {}
}

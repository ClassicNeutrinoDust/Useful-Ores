package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.item.NyxiumniteStaffItem;
import com.geckolib.renderer.GeoItemRenderer;

public class NyxiumniteStaffItemRenderer extends GeoItemRenderer<NyxiumniteStaffItem> {
    public NyxiumniteStaffItemRenderer() {
        super(new NyxiumniteStaffGeoModel());
    }
}


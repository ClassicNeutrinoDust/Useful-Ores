package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.item.MeteorStaffItem;
import com.geckolib.renderer.GeoItemRenderer;

public class MeteorStaffItemRenderer extends GeoItemRenderer<MeteorStaffItem> {

   public MeteorStaffItemRenderer() {
      super(new MeteorStaffGeoModel());
   }
}


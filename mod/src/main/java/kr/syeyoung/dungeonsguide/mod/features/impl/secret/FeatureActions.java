/*
 * Dungeons Guide - The most intelligent Hypixel Skyblock Dungeons Mod
 * Copyright (C) 2021  cyoung06
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package kr.syeyoung.dungeonsguide.mod.features.impl.secret;


import kr.syeyoung.dungeonsguide.mod.DungeonsGuide;
import kr.syeyoung.dungeonsguide.mod.SkyblockStatus;
import kr.syeyoung.dungeonsguide.mod.config.types.AColor;
import kr.syeyoung.dungeonsguide.mod.dungeon.DungeonContext;
import kr.syeyoung.dungeonsguide.mod.dungeon.actions.AbstractAction;
import kr.syeyoung.dungeonsguide.mod.dungeon.actions.tree.ActionRoute;
import kr.syeyoung.dungeonsguide.mod.dungeon.roomfinder.DungeonRoom;
import kr.syeyoung.dungeonsguide.mod.dungeon.roomprocessor.GeneralRoomProcessor;
import kr.syeyoung.dungeonsguide.mod.features.FeatureRegistry;
import kr.syeyoung.dungeonsguide.mod.features.text.DefaultTextHUDFeatureStyleFeature;
import kr.syeyoung.dungeonsguide.mod.features.text.DefaultingDelegatingTextStyle;
import kr.syeyoung.dungeonsguide.mod.features.text.NullTextStyle;
import kr.syeyoung.dungeonsguide.mod.features.text.TextHUDFeature;
import kr.syeyoung.dungeonsguide.mod.guiv2.elements.richtext.TextSpan;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

import java.awt.*;

public class FeatureActions extends TextHUDFeature {
    public FeatureActions() {
        super("Dungeon.Secrets", "Action Viewer", "View List of actions that needs to be taken", "secret.actionview");

        registerDefaultStyle("pathfinding", DefaultingDelegatingTextStyle.derive(() -> FeatureRegistry.DEFAULT_STYLE.getStyle(DefaultTextHUDFeatureStyleFeature.Styles.NAME)));
        registerDefaultStyle("mechanic", DefaultingDelegatingTextStyle.ofDefault().setTextShader(new AColor(0x55, 0xFF,0x55,255)).setBackgroundShader(new AColor(0, 0,0,0)));
        registerDefaultStyle("separator", DefaultingDelegatingTextStyle.derive(() -> FeatureRegistry.DEFAULT_STYLE.getStyle(DefaultTextHUDFeatureStyleFeature.Styles.SEPARATOR)));
        registerDefaultStyle("state", DefaultingDelegatingTextStyle.ofDefault().setTextShader(new AColor(0x55, 0xFF,0x55,255)).setBackgroundShader(new AColor(0, 0,0,0)));
        registerDefaultStyle("current", DefaultingDelegatingTextStyle.derive(() -> FeatureRegistry.DEFAULT_STYLE.getStyle(DefaultTextHUDFeatureStyleFeature.Styles.VALUE)));
        registerDefaultStyle("number", DefaultingDelegatingTextStyle.ofDefault().setTextShader( new AColor(0x00, 0xAA,0xAA,255)).setBackgroundShader(new AColor(0, 0,0,0)));
        registerDefaultStyle("dot", DefaultingDelegatingTextStyle.derive(() -> FeatureRegistry.DEFAULT_STYLE.getStyle(DefaultTextHUDFeatureStyleFeature.Styles.SEPARATOR)));
        registerDefaultStyle("action", DefaultingDelegatingTextStyle.derive(() -> FeatureRegistry.DEFAULT_STYLE.getStyle(DefaultTextHUDFeatureStyleFeature.Styles.VALUE)));
        registerDefaultStyle("afterline", DefaultingDelegatingTextStyle.ofDefault().setTextShader(new AColor(0xAA, 0xAA,0xAA,255)).setBackgroundShader(new AColor(0, 0,0,0)));
    }


    @Override
    public boolean isHUDViewable() {
        if (!SkyblockStatus.isOnDungeon()) return false;
        if (DungeonsGuide.getDungeonsGuide().getDungeonFacade().getContext() == null || DungeonsGuide.getDungeonsGuide().getDungeonFacade().getContext().getScaffoldParser() == null) return false;
        DungeonContext context = DungeonsGuide.getDungeonsGuide().getDungeonFacade().getContext();

        EntityPlayerSP thePlayer = Minecraft.getMinecraft().thePlayer;
        Point roomPt = context.getScaffoldParser().getDungeonMapLayout().worldPointToRoomPoint(thePlayer.getPosition());
        DungeonRoom dungeonRoom = context.getScaffoldParser().getRoomMap().get(roomPt);
        if (dungeonRoom == null) return false;
        return dungeonRoom.getRoomProcessor() instanceof GeneralRoomProcessor;
    }


    @Override
    public TextSpan getDummyText() {
        TextSpan dummyText = new TextSpan(new NullTextStyle(), "");
        dummyText.addChild(new TextSpan(getStyle("pathfinding"), "Pathfinding "));
        dummyText.addChild(new TextSpan(getStyle("mechanic"), "Secret "));
        dummyText.addChild(new TextSpan(getStyle("separator"), "-> "));
        dummyText.addChild(new TextSpan(getStyle("state"), "Found\n"));
        dummyText.addChild(new TextSpan(getStyle("current"), "> "));
        dummyText.addChild(new TextSpan(getStyle("number"), "1"));
        dummyText.addChild(new TextSpan(getStyle("dot"), ". "));
        dummyText.addChild(new TextSpan(getStyle("action"), "Move "));
        dummyText.addChild(new TextSpan(getStyle("afterline"), "OffsetPoint{x=1,y=42,z=1} \n"));
        dummyText.addChild(new TextSpan(getStyle("current"), "  "));
        dummyText.addChild(new TextSpan(getStyle("number"), "2"));
        dummyText.addChild(new TextSpan(getStyle("dot"), ". "));
        dummyText.addChild(new TextSpan(getStyle("action"), "Click "));
        dummyText.addChild(new TextSpan(getStyle("afterline"), "OffsetPoint{x=1,y=42,z=1} \n"));
        dummyText.addChild(new TextSpan(getStyle("current"), "  "));
        dummyText.addChild(new TextSpan(getStyle("number"), "3"));
        dummyText.addChild(new TextSpan(getStyle("dot"), ". "));
        dummyText.addChild(new TextSpan(getStyle("action"), "Profit "));
        return dummyText;
    }


    @Override
    public TextSpan getText() {
        TextSpan actualBit = new TextSpan(new NullTextStyle(), "");

        DungeonContext context = DungeonsGuide.getDungeonsGuide().getDungeonFacade().getContext();

        EntityPlayerSP thePlayer = Minecraft.getMinecraft().thePlayer;
        Point roomPt = context.getScaffoldParser().getDungeonMapLayout().worldPointToRoomPoint(thePlayer.getPosition());
        DungeonRoom dungeonRoom = context.getScaffoldParser().getRoomMap().get(roomPt);

        for (ActionRoute path : ((GeneralRoomProcessor) dungeonRoom.getRoomProcessor()).getPath().values()) {
            actualBit.addChild(new TextSpan(getStyle("pathfinding"), "Pathfinding "));
            actualBit.addChild(new TextSpan(getStyle("mechanic"), path.getMechanic()+" "));
            actualBit.addChild(new TextSpan(getStyle("separator"), "-> "));
            actualBit.addChild(new TextSpan(getStyle("state"), path.getState()+"\n"));

            for (int i = Math.max(0,path.getCurrent()-2); i < path.getActions().size(); i++) {
                actualBit.addChild(new TextSpan(getStyle("current"), (i == path.getCurrent() ? ">" : " ") +" "));
                actualBit.addChild(new TextSpan(getStyle("number"), i+""));
                actualBit.addChild(new TextSpan(getStyle("dot"), ". "));
                AbstractAction action = path.getActions().get(i);
                String[] str = action.toString().split("\n");
                actualBit.addChild(new TextSpan(getStyle("action"), str[0] + " "));
                actualBit.addChild(new TextSpan(getStyle("afterline"), "("));
                for (int i1 = 1; i1 < str.length; i1++) {
                    String base = str[i1].trim();
                    if (base.startsWith("-"))
                        base = base.substring(1);
                    base = base.trim();
                    actualBit.addChild(new TextSpan(getStyle("afterline"), base+" "));
                }
                actualBit.addChild(new TextSpan(getStyle("afterline"), ")\n"));
            }
        }
        return actualBit;
    }
}

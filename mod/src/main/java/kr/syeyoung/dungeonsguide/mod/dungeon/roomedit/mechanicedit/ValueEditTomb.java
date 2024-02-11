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

package kr.syeyoung.dungeonsguide.mod.dungeon.roomedit.mechanicedit;

import kr.syeyoung.dungeonsguide.dungeon.data.OffsetPoint;
import kr.syeyoung.dungeonsguide.dungeon.data.OffsetPointSet;
import kr.syeyoung.dungeonsguide.dungeon.mechanics.DungeonTomb;
import kr.syeyoung.dungeonsguide.mod.dungeon.roomedit.EditingContext;
import kr.syeyoung.dungeonsguide.mod.dungeon.roomedit.Parameter;
import kr.syeyoung.dungeonsguide.mod.dungeon.roomedit.valueedit.ValueEdit;
import kr.syeyoung.dungeonsguide.mod.dungeon.roomedit.valueedit.ValueEditCreator;
import kr.syeyoung.dungeonsguide.mod.features.FeatureRegistry;
import kr.syeyoung.dungeonsguide.mod.features.impl.etc.FeatureCollectDungeonRooms;
import kr.syeyoung.dungeonsguide.mod.gui.MPanel;
import kr.syeyoung.dungeonsguide.mod.gui.elements.*;
import kr.syeyoung.dungeonsguide.mod.utils.TextUtils;
import net.minecraft.init.Blocks;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ValueEditTomb extends MPanel implements ValueEdit<DungeonTomb> {
    private Parameter parameter;

    // scroll pane
    // just create
    // add set
    private final DungeonTomb dungeonTomb;

    private final MLabel label;
    private final MValue<OffsetPointSet> value;
    private final MTextField preRequisite;
    private final MLabelAndElement preRequisite2;
    private final MButton expand;
    private final MButton updateOnlyAir;

    public ValueEditTomb(final Parameter parameter2) {
        this.parameter = parameter2;
        this.dungeonTomb = (DungeonTomb) parameter2.getNewData();


        label = new MLabel();
        label.setText("Tomb Points");
        label.setAlignment(MLabel.Alignment.LEFT);
        add(label);

        value = new MValue(dungeonTomb.getSecretPoint(), Collections.emptyList());
        add(value);

        expand = new MButton();
        expand.setText("Expand");
        expand.setBackgroundColor(Color.green);
        expand.setForeground(Color.black);
        expand.setBounds(new Rectangle(0,40,getBounds().width, 20));
        add(expand);
        expand.setOnActionPerformed(new Runnable() {
            @Override
            public void run() {
                for (FeatureCollectDungeonRooms.RoomInfo.BlockUpdate blockUpdate : FeatureRegistry.ADVANCED_ROOMEDIT.getBlockUpdates()) {
                    boolean found = false;
                    for (FeatureCollectDungeonRooms.RoomInfo.BlockUpdate.BlockUpdateData updatedBlock : blockUpdate.getUpdatedBlocks()) {
                        if (updatedBlock.getPos().equals(dungeonTomb.getSecretPoint().getOffsetPointList().get(0).getBlockPos(EditingContext.getEditingContext().getRoom()))
                                && updatedBlock.getBlock().getBlock() == Blocks.air) {
                            found = true;
                            break;
                        }
                    }
                    if (found) {
                        for (FeatureCollectDungeonRooms.RoomInfo.BlockUpdate.BlockUpdateData updatedBlock : blockUpdate.getUpdatedBlocks()) {
                            OffsetPoint pt = new OffsetPoint(EditingContext.getEditingContext().getRoom(), updatedBlock.getPos());
                            if (!dungeonTomb.getSecretPoint().getOffsetPointList().contains(pt))
                                dungeonTomb.getSecretPoint().getOffsetPointList().add(pt);
                        }
                    }
                }

            }
        });

        updateOnlyAir = new MButton();
        updateOnlyAir.setText("Update Air");
        updateOnlyAir.setBackgroundColor(Color.green);
        updateOnlyAir.setForeground(Color.black);
        updateOnlyAir.setBounds(new Rectangle(0,40,getBounds().width, 20));
        add(updateOnlyAir);

        updateOnlyAir.setOnActionPerformed(new Runnable() {
            @Override
            public void run() {
                OffsetPointSet ofs = dungeonTomb.getSecretPoint();
                List<OffsetPoint> filtered = new ArrayList<OffsetPoint>();
                for (OffsetPoint offsetPoint : ofs.getOffsetPointList()) {
                    if (offsetPoint.getBlock(EditingContext.getEditingContext().getRoom()) != Blocks.air) continue;
                    filtered.add(offsetPoint);
                }
                dungeonTomb.getSecretPoint().setOffsetPointList(filtered);
            }
        });

        preRequisite = new MTextField() {
            @Override
            public void edit(String str) {
                dungeonTomb.setPreRequisite(Arrays.asList(str.split(",")));
            }
        };
        preRequisite.setText(TextUtils.join(dungeonTomb.getPreRequisite(), ","));
        preRequisite2 = new MLabelAndElement("Req.",preRequisite);
        preRequisite2.setBounds(new Rectangle(0,40,getBounds().width,20));
        add(preRequisite2);
    }

    @Override
    public void onBoundsUpdate() {
        label.setBounds(new Rectangle(0,0,getBounds().width, 20));
        value.setBounds(new Rectangle(0,20,getBounds().width, 20));
        updateOnlyAir.setBounds(new Rectangle(0, 40, getBounds().width, 20));
        expand.setBounds(new Rectangle(0, 60, getBounds().width, 20));
        preRequisite2.setBounds(new Rectangle(0,40,getBounds().width,20));
    }

    @Override
    public void setParameter(Parameter parameter) {
        this.parameter = parameter;
    }

    @Override
    public void renderWorld(float partialTicks) {
        dungeonTomb.highlight(new Color(0,255,255,50), parameter.getName(), EditingContext.getEditingContext().getRoom(), partialTicks);
    }

    @Override
    public void resize(int parentWidth, int parentHeight) {
        this.setBounds(new Rectangle(0,0,parentWidth, parentHeight));
    }

    public static class Generator implements ValueEditCreator<ValueEditTomb> {

        @Override
        public ValueEditTomb createValueEdit(Parameter parameter) {
            return new ValueEditTomb(parameter);
        }

        @Override
        public Object createDefaultValue(Parameter parameter) {
            return new DungeonTomb();
        }

        @Override
        public Object cloneObj(Object object) {
            try {
                return ((DungeonTomb)object).clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            assert false;
            return null;
        }
    }
}

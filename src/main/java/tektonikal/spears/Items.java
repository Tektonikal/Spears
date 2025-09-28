package tektonikal.spears;

import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class Items {
    public static final Item IRON_SPEAR = register(
            new SpearItem(new Item.Settings().attributeModifiers(SpearItem.createAttributeModifiers(ToolMaterials.IRON)), ToolMaterials.IRON),
            "iron_spear"
    );
    public static Item register(Item item, String id) {
        Identifier itemID = Identifier.of(Spears.MOD_ID, id);
        return Registry.register(Registries.ITEM, itemID, item);
    }
    public static void init(){
        //NOP
    }
}
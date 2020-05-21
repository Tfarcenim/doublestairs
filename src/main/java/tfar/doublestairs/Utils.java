package tfar.doublestairs;

import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Utils {
	public static NonNullList<Ingredient> deserializeIngredients(String[] pattern, Map<String, Ingredient> keys, int patternWidth, int patternHeight) {
		NonNullList<Ingredient> nonnulllist = NonNullList.withSize(patternWidth * patternHeight, Ingredient.EMPTY);
		Set<String> set = Sets.newHashSet(keys.keySet());
		set.remove(" ");

		for(int i = 0; i < pattern.length; ++i) {
			for(int j = 0; j < pattern[i].length(); ++j) {
				String s = pattern[i].substring(j, j + 1);
				Ingredient ingredient = keys.get(s);
				if (ingredient == null) {
					throw new JsonSyntaxException("Pattern references symbol '" + s + "' but it's not defined in the key");
				}

				set.remove(s);
				nonnulllist.set(j + patternWidth * i, ingredient);
			}
		}

		if (!set.isEmpty()) {
			throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
		} else {
			return nonnulllist;
		}
	}

	public static void addSlabReverse(Map<ResourceLocation, IRecipe<?>> craftingRecipes){

		Collection<IRecipe<?>> iRecipeCollection = craftingRecipes.values().stream().filter(iRecipe -> {
			ItemStack output = iRecipe.getRecipeOutput();
			return Block.getBlockFromItem(output.getItem()) instanceof SlabBlock && output.getCount() == 6;
		}).collect(Collectors.toList());

		iRecipeCollection.forEach(iRecipe -> {

			Block slab = Block.getBlockFromItem(iRecipe.getRecipeOutput().getItem());

			Block full = Block.getBlockFromItem(iRecipe.getIngredients().get(0).getMatchingStacks()[0].getItem());

			ResourceLocation id = new ResourceLocation(slab.getRegistryName().getNamespace(),slab.getRegistryName().getPath()+"_reverse");
			Ingredient slabIngredient = Ingredient.fromItems(slab.asItem());
			String[] pattern = new String[]{"a","a"};
			Map<String,Ingredient> map = new HashMap<>();
			map.put("a",slabIngredient);
			NonNullList<Ingredient> ingredients = Utils.deserializeIngredients(pattern,map,1,2);

			ItemStack stack =new ItemStack(full);

			IRecipe<?> recipe = new ShapedRecipe(id,"",1,2,
							ingredients,stack);
			craftingRecipes.put(id,recipe);
		});
	}
}

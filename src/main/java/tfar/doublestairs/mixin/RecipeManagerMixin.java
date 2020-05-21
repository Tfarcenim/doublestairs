package tfar.doublestairs.mixin;

import com.google.gson.JsonObject;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.doublestairs.Utils;

import java.util.HashMap;
import java.util.Map;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {
	@Shadow private Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> recipes;

	@Inject(method = "apply",at = @At("RETURN"))
	private void doubleStairs(Map<ResourceLocation, JsonObject> splashList, IResourceManager resourceManagerIn, IProfiler profilerIn, CallbackInfo ci){
		recipes = new HashMap<>(recipes);
		recipes.put(IRecipeType.CRAFTING,new HashMap<>(recipes.get(IRecipeType.CRAFTING)));
		recipes.put(IRecipeType.STONECUTTING,new HashMap<>(recipes.get(IRecipeType.STONECUTTING)));
		final Map<ResourceLocation,IRecipe<?>> craftingRecipes = recipes.get(IRecipeType.CRAFTING);
		craftingRecipes.values().forEach(iRecipe -> {
			if (iRecipe.getRecipeOutput().getCount() == 4 && iRecipe.getRecipeOutput().getItem().isIn(ItemTags.STAIRS)
							&& iRecipe.getClass() == ShapedRecipe.class){
				iRecipe.getRecipeOutput().setCount(8);
			}
		});
		Utils.addSlabReverse(craftingRecipes);
	}
}

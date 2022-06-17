package com.fedebonel.recipemvc.controllers;

import com.fedebonel.recipemvc.commands.IngredientCommand;
import com.fedebonel.recipemvc.commands.RecipeCommand;
import com.fedebonel.recipemvc.commands.UnitOfMeasureCommand;
import com.fedebonel.recipemvc.model.Ingredient;
import com.fedebonel.recipemvc.model.Recipe;
import com.fedebonel.recipemvc.services.IngredientService;
import com.fedebonel.recipemvc.services.RecipeService;
import com.fedebonel.recipemvc.services.UnitOfMeasureService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Disabled
class IngredientControllerTest {

    @Mock
    IngredientService ingredientService;

    @Mock
    RecipeService recipeService;
    @Mock
    UnitOfMeasureService unitOfMeasureService;

    IngredientController ingredientController;


    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ingredientController = new IngredientController(recipeService, ingredientService, unitOfMeasureService);
        mockMvc = MockMvcBuilders.standaloneSetup(ingredientController).build();
    }

    @Test
    void testListIngredients() throws Exception {
        RecipeCommand recipeCommand = new RecipeCommand();
        recipeCommand.setId("1L");
        when(recipeService.findCommandById(recipeCommand.getId())).thenReturn(Mono.just(recipeCommand));

        mockMvc.perform(get("/recipe/" + recipeCommand.getId() + "/ingredients"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/ingredient/list"))
                .andExpect(model().attributeExists("recipe"));
    }

    @Test
    void showIngredient() throws Exception {
        IngredientCommand command = new IngredientCommand();
        command.setId("1L");
        when(ingredientService.findCommandById(anyString(), anyString())).thenReturn(Mono.just(command));

        mockMvc.perform(get("/recipe/1/ingredient/" + command.getId() + "/show"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/ingredient/show"))
                .andExpect(model().attributeExists("ingredient"));
    }

    @Test
    void createRecipeIngredientForm() throws Exception {
        RecipeCommand recipe = new RecipeCommand();
        recipe.setId("1L");

        when(recipeService.findCommandById(recipe.getId())).thenReturn(Mono.just(recipe));
        when(unitOfMeasureService.listAllUOM()).thenReturn(Flux.just(new UnitOfMeasureCommand()));

        mockMvc.perform(get("/recipe/" + recipe.getId() + "/ingredient/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/ingredient/ingredientform"))
                .andExpect(model().attributeExists("ingredient", "uomList"));

        verify(recipeService).findCommandById(recipe.getId());
        verify(unitOfMeasureService).listAllUOM();
    }

    @Test
    void updateRecipeIngredientForm() throws Exception {
        IngredientCommand command = new IngredientCommand();
        command.setId("1L");
        UnitOfMeasureCommand uom1 = new UnitOfMeasureCommand();
        uom1.setId("2L");

        when(ingredientService.findCommandById(anyString(), anyString())).thenReturn(Mono.just(command));
        when(unitOfMeasureService.listAllUOM()).thenReturn(Flux.just(uom1));

        mockMvc.perform(get("/recipe/1/ingredient/" + command.getId() + "/update"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/ingredient/ingredientform"))
                .andExpect(model().attributeExists("ingredient", "uomList"));
    }

    @Test
    void saveOrUpdate() throws Exception {
        IngredientCommand ingredientCommand = new IngredientCommand();
        ingredientCommand.setId("1L");
        ingredientCommand.setRecipeId("2L");
        UnitOfMeasureCommand unitOfMeasure = new UnitOfMeasureCommand();

        when(unitOfMeasureService.findById(anyString())).thenReturn(Mono.just(unitOfMeasure));
        when(ingredientService.saveCommand(any())).thenReturn(Mono.just(ingredientCommand));

        mockMvc.perform(post("/recipe/" + ingredientCommand.getRecipeId() + "/ingredient")
                        .param("uom.id", "uom!id"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view()
                        .name("redirect:/recipe/"
                                + ingredientCommand.getRecipeId()
                                + "/ingredients/"));
    }

    @Test
    void deleteRecipeIngredient() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setId("1L");
        Ingredient ingredient = new Ingredient();
        ingredient.setId("2L");

        when(ingredientService.deleteById(recipe.getId(), ingredient.getId())).thenReturn(Mono.empty());

        mockMvc.perform(get("/recipe/" + recipe.getId() + "/ingredient/" + ingredient.getId() + "/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/recipe/" + recipe.getId() + "/ingredients"));

        verify(ingredientService).deleteById(anyString(), anyString());
    }
}
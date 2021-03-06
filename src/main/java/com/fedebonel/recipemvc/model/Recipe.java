package com.fedebonel.recipemvc.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Recipe POJO
 */
@Data
@EqualsAndHashCode(exclude = {"likedBy"})
@Entity
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private Integer prepTime;
    private Integer cookTime;
    private Integer servings;
    private String source;
    private String url;

    @Lob
    private String directions;

    @Enumerated(value = EnumType.STRING)
    private Difficulty difficulty;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "recipe")
    private Set<Ingredient> ingredients = new HashSet<>();

    @Lob
    private Byte[] image;

    @OneToOne(cascade = CascadeType.ALL)
    private Notes note;

    @ManyToMany
    @JoinTable(name = "recipe_category",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories = new HashSet<>();

    @ManyToMany(mappedBy = "likedRecipes")
    private Set<User> likedBy = new HashSet<>();

    public void addIngredient(Ingredient ingredient) {
        ingredient.setRecipe(this);
        this.ingredients.add(ingredient);
    }

    public void removeIngredient(Ingredient ingredient) {
        Ingredient ingredientToDelete = ingredients.stream()
                .filter(recipeIngredient -> recipeIngredient.getId().equals(ingredient.getId())).findFirst().orElse(null);

        if (ingredientToDelete != null) ingredientToDelete.setRecipe(null);

        ingredients.remove(ingredientToDelete);
    }

    public void setNote(Notes note) {
        if (note == null) return;
        note.setRecipe(this);
        this.note = note;
    }

    public void likeByUser(User user) {
        if(!likedBy.contains(user)) {
            likedBy.add(user);
        } else {
            likedBy.remove(user);
        }
    }

    @Override
    public String toString() {
        return "Recipe{" +
                ", description='" + description + '\'' +
                ", prepTime=" + prepTime +
                ", cookTime=" + cookTime +
                ", servings=" + servings +
                ", source='" + source + '\'' +
                ", url='" + url + '\'' +
                ", directions='" + directions + '\'' +
                ", difficulty=" + difficulty +
                '}';
    }
}

package me.donghun.item2;

public class NutritionFacts {

    private final int servingSize;
    private final int servings;
    private final int calories;
    private final int fat;

    public static class Builder {
        // 필수 매개변수
        private final int servingSize;
        private final int servings;

        // 선택 매개변수(기본값으로 초기화)
        private int calories = 0;
        private int fat = 0;

        public Builder(int servingSize, int servings) {
            this.servingSize = servingSize;
            this.servings = servings;
        }

        public Builder calories(int val) {
            calories = val;
            return this;
        }

        public Builder fat(int val) {
            fat = val;
            return this;
        }

        public NutritionFacts build() {
            return new NutritionFacts(this);
        }

    }

    private NutritionFacts(Builder builder) {
        servingSize = builder.servingSize;
        servings = builder.servings;
        calories = builder.calories;
        fat = builder.fat;
    }

    public static void main(String[] args) {
        NutritionFacts nutritionFacts = new NutritionFacts.Builder(100, 200).calories(15).build();
    }

    // 자바빈즈 패턴(일관성 이슈가 있음)

//    public NutritionFacts() {
//    }
//
//    public void setServingSize(int servingSize) { this.servingSize = servingSize; }
//    public void setServings(int servings) { this.servings = servings; }
//    public void setCalories(int calories) { this.calories = calories; }
//    public void setFat(int fat) { this.fat = fat; }

    // 점층적 생성자 패턴(확장성이 좋지 않음)
//
//    public NutritionFacts(int servingSize, int servings) {
//        this(servingSize, servings, 0);
//    }
//
//    public NutritionFacts(int servingSize, int servings, int calories) {
//        this(servingSize, servings, calories, 0);
//    }
//
//    public NutritionFacts(int servingSize, int servings, int calories, int fat) {
//        this.servingSize = servingSize;
//        this.servings = servings;
//        this.calories = calories;
//        this.fat = fat;
//    }

}
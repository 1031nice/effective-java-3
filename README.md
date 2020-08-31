# effective-java-3
코드를 구성하는 관례적이고 효과적인 방법에 관한 책. **Best Practice**.
그 방법의 방향은 이해하기 쉬운 코드, 수정과 개선이 수월한 코드.
이 책은 총 90개의 아이템을 담았다. 각각의 아이템이 하나의 규칙을 다루며,
각 규칙은 업계 최고의 베테랑 프로그래머들이 유익하다고 인정하는 관례다.

## Table of Contents
[blog](http://www.tistory.com/live-everyday)
1. [Item1 생성자 대신 정적 팩토리 메소드를 고려하라](#Item1-생성자-대신-정적-팩토리-메소드를-고려하라)
2. [Item2 생성자에 매개변수가 많다면 빌더를 고려하라](#Item2-생성자에-매개변수가-많다면-빌더를-고려하라)

### Item1 생성자 대신 정적 팩토리 메소드를 고려하라

static factory method: 그 클래스의 인스턴스를 반환하는 단순 정적 메서드.

public 생성자 대신 (또는 함께) 정적 팩토리 메서드를 제공하면

> #### 장점1. 이름을 가질 수 있다.

하나의 시그니처로는 생성자를 하나만 만들 수 있다. 입력 매개변수의 순서나 개수가 유일하지 않으면 그만큼 많은 생성자를 만들어야 한다. 엉뚱한 것을 호출하는 실수를 할 수도 있다.

한 클래스에 시그니처가 같은 생성자가 여러 개 필요할 것 같으면, 생성자를 정적 팩토리 메서드로 바꾸고 각각의 차이를 잘 드러내는 이름을 지어주자.

> #### 장점2. 호출될 때마다 인스턴스를 새로 생성하지는 않아도 된다.

new 연산자를 통해 생성자로 인스턴스를 생성하면 매번 새로운 인스턴스가 생성되지만, 정적 팩토리 메서드를 사용하면 불필요한 객체 생성을 피할 수 있다. 다시 말해 인스턴스를 통제할 수 있다. (캐싱, 싱글턴, 인스턴스화 불가 ...)

> #### 장점3. 반환 타입의 하위 타입 객체를 반환할 수 있는 능력이 있다.
API를 만들 때 이 유연성을 응용하면 구현 클래스를 공개하지 않아도 그 객체를 반환할 수 있어 API를 작게 유지할 수 있다. 덕분에 프로그래머가 API를 사용하기 위해 익혀야하는 개념의 수와 난이도도 낮아졌다(인터페이스만 신경쓰면 되므로).

> #### 장점4. 입력 매개변수에 따라 매번 다른 클래스의 객체를 반환할 수 있다.
반환 타입의 하위 타입이기만 하면 어떤 클래스의 객체를 반환하든 상관없다.

> #### 장점5. 정적 팩토리 메서드를 작성하는 시점에는 반환할 객체의 클래스가 존재하지 않아도 된다.

---

> #### 단점1. 정적 팩토리 메서드만 제공할 경우 하위 클래스를 만들 수 없다.
상속을 하려면 public 또는 protected 생성자가 필요하기 때문이다.

> #### 단점2. 정적 팩토리 메서드는 프로그래머가 찾기 어렵다.
생성자처럼 API 설명에 명확히 드러나지 않기 때문에 사용자는 정적 팩토리 메서드 방식 클래스를 인스턴스화할 방법을 알아내야 한다.

---

> ####흔히 사용하는 명명 방식

**from**: 매개변수 하나를 받아 해당 타입의 인스턴스 반환

`Date d = Date.from(instant);`

**of**: 여러 매개변수를 받아 적절한 타입의 인스턴스를 반환

`Set<Rank> faceCards = EnumSet.of(JACK, QUEEN, KING);`

**valueOf**: from과 of의 더 자세한 버전

`Integer integer = Integer.valueOf(“5”);`

**instance 혹은 getInstance**: (매개변수를 받는다면) 매개변수로 명시한 인스턴스를 반환하지만, 같은 인스턴스임을 보장하지는 않음

`StackWalker luke = StackWalker.getInstance(options);`

**create 혹은 newInstance**: instance 혹은 getInstance와 같지만, 매번 새로운 인스턴스를 생성해 반환

`Object newArray = Array.newInstance(classObject, arrayLen);`

**getType**: getInstance와 같으나, 다른 클래스에 팩토리 메서드를 정의할 때 사용(“Type”은 팩토리 메서드가 반환할 객체의 타입)

`FileStore fs = Files.getFileStore(path)`

**newType**: newInstance와 같으나, 다른 클래스에 팩토리 메서드를 정의할 때 사용(“Type”은 팩토리 메서드가 반환할 객체의 타입)

`BufferedReader br = Files.newBufferedReader(path);`

**type**: getType과 newType의 간결한 버전

`List<Complaint> litany = Collections.list(legacyLitany);`

### Item2 생성자에 매개변수가 많다면 빌더를 고려하라

정적 팩토리와 생성자에는 똑같은 제약이 하나 있다. 선택적 매개변수가 많을 때 적절히 대응하기 어렵다는 점이다. 식품 포장의 영양정보를 표현하는 클래스를 예로 들어보자.

```java
public class NutritionFacts {

        private final int servingSize; // 필수
        private final int servings; // 필수
        private final int calories; // 선택
        private final int fat; // 선택
    
        // 점층적 생성자 패턴(확장성이 좋지 않음)
    
        public NutritionFacts(int servingSize, int servings) {
            this(servingSize, servings, 0);
        }
    
        public NutritionFacts(int servingSize, int servings, int calories) {
            this(servingSize, servings, calories, 0);
        }
    
        public NutritionFacts(int servingSize, int servings, int calories, int fat) {
            this.servingSize = servingSize;
            this.servings = servings;
            this.calories = calories;
            this.fat = fat;
        }

}
```

위의 코드는 선택적 매개변수가 많을 때 프로그래머들이 즐겨 사용했던 **점층적 사용자 패턴**이다. 

하지만 이런 패턴은 확장하기도 불편하고, 경우의 수 만큼 생성자를 만들지 않는 이상 사용자가 설정하길 원치 않는 매개변수까지 포함하기 쉽다는 문제가 있다.
뿐만 아니라 클라이언트 코드를 작성하거나 읽기도 어렵다. 클라이언트가 실수로 (같은 타입) 매개변수의 순서를 바꿔 건네도 컴파일러가 알아차리지 못할 수도 있다.

선택 매개변수가 많을 때 활용할 수 있는 또 다른 방법은 **자바빈즈 패턴**이다.
매개변수가 없는 생성자로 객체를 만든 후, setter 메소드들을 호출해 원하는 매개변수의 값을 설정하는 방식이다.

```java
public class NutritionFacts {

    // 자바빈즈 패턴(일관성 이슈가 있음)

    private int servingSize = -1; // 필수
    private int servings = -1; // 필수
    private int calories = 0; // 선택
    private int fat = 0; // 선택

    public NutritionFacts() {
    }

    public void setServingSize(int servingSize) { this.servingSize = servingSize; }
    public void setServings(int servings) { this.servings = servings; }
    public void setCalories(int calories) { this.calories = calories; }
    public void setFat(int fat) { this.fat = fat; }

}
```

코드가 길어지긴 했지만 인스턴스를 만들기 쉽고, 더 읽기 쉽다. 하지만 자바빈즈도 단점이 존재한다.

객체 하나를 만들려면 메소드를 여러 개 호출해야 한다. 또한 객체가 완전히 생성되기 전까지 일관성이 무너진 상태에 놓이게 된다.
점층적 생성자 패턴에서는 매개변수들이 유효한지를 생성자에서만 확인하면 일관성을 유지할 수 있었는데,
그 장치가 완전히 사라진 것이다. 일관성이 깨진 객체가 만들어지면, 버그를 심은 코드와 그 버그 때문에
런타임에 문제를 겪는 코드가 물리적으로 멀리 떨어져 있을 것이므로 디버깅도 만만치 않다. 이처럼 일관성이 무너지는 문제 때문에
자바빈즈 패턴에서는 클래스를 불변으로 만들 수 없으며 스레드 안전성을 얻으려면 추가 작업이 필요하다.

점층적 생성자 패턴의 안전성과 자바빈즈 패턴의 가독성을 겸비한 빌더 패턴을 살펴보자.
클라이언트는 필요한 객체를 직접 만드는 대신, 필수 매개변수만으로 생성자(혹은 정적 팩토리)를 호출해 빌더 객체를 얻는다.
그런 다음 빌더 객체가 제공하는 일종의 setter 메소드로 원하는 선택 매개변수를 설정한다.
마지막으로 매개변수가 없는 build 메소드를 호출해 필요한(보통 불변인) 객체를 얻는다.
빌더는 생성할 클래스 안에 정적 멤버 클래스로 만들어두는 게 보통이다.

```java
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
}
```

NutritionFacts 클래스는 불변이며, 모든 매개변수의 기본값들을 한곳에 모아뒀다.
빌더의 setter 메소드들은 빌더 자신을 반환하기 때문에 연쇄적으로 호출할 수 있다.
이런 방식을 메소드 호출이 흐르듯 연결된다는 뜻으로 **fluent API** 혹은 **method chaining**라 한다.
다음은 이 클래스를 사용하는 클라이언트 코드다.

`NutritionFacts cocaCola = new NutiritionFacts.Builder(240, 8).calories(100).build();`

쓰기 쉽고, 읽기 쉽다.

생성자나 정적 팩토리가 처리해야 할 매개변수가 많다면 빌더 패턴을 선택하는 게 더 낫다.
매개변수 중 다수가 필수가 아니거나 같은 타입이면 특히 더 그렇다. 빌더는 점층적 생성자보다
클라이언트 코드를 읽고 쓰기가 훨씬 간결하고, 자바빈즈보다 훨씬 안전하다.
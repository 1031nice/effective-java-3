# effective-java-3
코드를 구성하는 관례적이고 효과적인 방법에 관한 책. **Best Practice**.
그 방법의 방향은 이해하기 쉬운 코드, 수정과 개선이 수월한 코드.
이 책은 총 90개의 아이템을 담았다. 각각의 아이템이 하나의 규칙을 다루며,
각 규칙은 업계 최고의 베테랑 프로그래머들이 유익하다고 인정하는 관례다.

## Table of Contents
[blog](http://live-everyday.tistory.com)
1. [Item1 생성자 대신 정적 팩토리 메소드를 고려하라](#Item1-생성자-대신-정적-팩토리-메소드를-고려하라)
2. [Item2 생성자에 매개변수가 많다면 빌더를 고려하라](#Item2-생성자에-매개변수가-많다면-빌더를-고려하라)
3. [Item3 private 생성자나 열거 타입으로 싱글턴임을 보증하라](#Item3-private-생성자나-열거-타입으로-싱글턴임을-보증하라)

## 2장 객체 생성과 파괴

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

### Item3 private 생성자나 열거 타입으로 싱글턴임을 보증하라

싱글턴이란 인스턴스를 오직 하나만 생성할 수 있는 클래스를 말한다. 싱글턴의 전형적인 예로는
함수와 같은 무상태 객체나 설계상 유일해야 하는 시스템 컴포넌트를 들 수 있다.
그런데 클래스를 싱글턴으로 만들면 이를 사용하는 클라이언트를 테스트하기가 어려워질 수 있다.
목(mock) 객체로 대체할 수 없기 때문이다.

음.. 제대로된 싱글턴을 만들기란 까다로워 보인다.
스프링이 얼마나 유용한지 새삼 느껴진다.
이 부분은 따로 정리하지 않고 일단 읽고 넘어가도록 하자.

### Item4 인스턴스화를 막으려거든 private 생성자를 사용하라

### Item5 자원을 직접 명시하지 말고 의존 객체 주입을 사용하라
의존 객체 주입이라 하는 이 기법은 클래스의 유연성, 재사용성, 테스트 용이성을 기막히게 개선해준다.

### Item6 불필요한 객체 생성을 피하라
생성자는 호출할 때마다 새로운 객체를 만들지만, 팩터리 메서드는 전혀 그렇지 않다.

박싱된 기본 타입보다는 기본 타입을 사용하고, 의도치 않은 오토박싱이 숨어들지 않도록 주의하자.

정규표현식에 사용되는 `Pattern` 인스턴스와 같이 생성 비용이 아주 비싼 객체는 캐싱하여 재사용하는 것이 좋다.

### Item7 다 쓴 객체 참조를 해제하라
참조를 다 썼을 때 `null` 처리(참조 해제)하면 된다. 다 쓴 참조를 `null` 처리하면 다른 이점도 따라온다.
만약 `null` 처리한 참조를 실수로 사용하려 하면 프로그램은 즉시 `NullPointerException`을 던지며 종료된다.
프로그램 오류는 가능한 한 조기에 발견하는 게 좋다.

하지만 모든 객체에 대해 다 쓰자마자 `null` 처리를 할 필요는 없다.
객체 참조를 `null` 처리하는 일은 예외적인 경우이고, 다 쓴 참조를 해제하는
가장 좋은 방법은 그 참조를 담은 변수를 유효 범위 밖으로 밀어내는 것인데 이는 보통 자연스럽게 이뤄진다.

자기 메모리를 직접 관리하는 클래스(예를 들면 Stack)라면 프로그래머는 항상
메모리 누수에 주의해야 하며 다 사용한 즉시 해당 객체를 `null` 처리해줘야 한다.

캐시, 리스너, 콜백 역시 메모리 누수의 주범이다.

### Item8 finalizer와 cleaner 사용을 피하라
PASS!

### Item9 try-finally보다는 try-with-resources를 사용하라
자원 닫기는 클라이언트가 놓치기 쉬워서 예측할 수 없는 성능 문제로 이어지기도 한다.
전통적으로 자원이 제대로 닫힘을 보장하는 수단으로 try-finally가 쓰였다.
하지만 자원이 둘 이상만 되어도 코드가 매우 지저분해진다.
자바 7에서 등장한 try-with-resources를 쓰자.
참고로 이 구조를 사용하려면 해당 자원이 `AutoCloseable` 인터페이스를 구현해야 한다.

```java
static String firstLineOfFile(String path) throws IOException {
    try (BufferedReader br = new BefferedReader(new FileReader(path))) {
        return br.readLine();
    }
}
```

try-with-resources는 짧고 읽기도 수월할 뿐 아니라 문제를 진단하기도 훨씬 좋다.
예를 들어, try-finally 문의 경우 try 블록과 finally 블록 모두에서 예외가 발생했을 때
나중에 발생한 예외가 처음 발생한 예외를 집어삼켜 버린다.
스택 추적 내역에도 첫 예외에 대한 정보가 남지 않는다. 반면 try-wtih-resources를 사용하면
처음 발생한 예외 하나만 기록되고 나중에 발생한 예외도 버려지는 것이 아니라
스택 추적 내역에 '숨겨졌다(suppressed)'는 꼬리표를 달고 출력된다.
자바 7에서 `Throwable`에 추가된 getSuppressed 메서드를 이용하면 프로그램 코드에서 가져올 수도 있다.

또한 try-with-resources에서도 보통의 try-finally에서처럼 catch 절을 쓸 수 있다.
catch 절 덕분에 try 문을 더 중첩하지 않고도 다수의 예외를 처리할 수 있다.

## 3장 모든 객체의 공통 메서드

`Object`에서 `final`이 아닌 메서드(equals, hashCode, toString, clone, finalize)는
모두 재정의를 염두에 두고 설계된 것이라 재정의 시 지켜야 하는 일반 규약이 명확히 정의되어 있다.
따라서 모든 클래스는 이 메서드들을 일반 규약에 맞게 재정의해야 한다.
이번 장에서는 이 메서드들을 언제 어떻게 재정의해야 하는지를 다룬다.

### Item10 equals는 일반 규약을 지켜 재정의하라
equals를 재정의해야 할 때는 언제일까? 객체 식별성(두 객체가 물리적으로 같은가)이 아니라
논리적 동치성을 확인해야 하는데, 상위 클래스의 equals가 논리적 동치성을 비교하도록 재정의되지 않았을 때다.

equals 메서드를 재정의할 때 따라야 하는 일반 규약(`Object` 명세에 적힌 규약)
* 반사성: x equals x는 `true`
* 대칭성: x equals y가 `true`면 반대도 `true`
* 추이성: x equals y, y equals z이면 x equals z
* 일관성: x equals y를 반복해서 호출하더라도 일관된 결과를 반환
* `null`-아님: x.equals(null)은 `false`다.
<br> (단, 참조 값 x, y, z는 `null`이 아님)
  
### Item11 equals를 재정의하려거든 hashCode도 재정의하라
Object 명세에서 발췌한 규약
* equals 비교에 사용되는 정보가 변경되지 않았다면, 애플리케이션이 실행되는 동안
그 객체의 hashCode 메서드는 몇 번을 호출해도 일관되게 값을 반환해야 한다.
* equals가 두 객체를 같다고 판단했다면, 두 객체의 hashCode는 똑같은 값을 반환해야 한다.
<br> (equals 비교에 사용되지 않은 필드는 반드시 해시코드 계산에서 제외해야 한다.)
* equals가 두 객체를 다르다고 판단했더라도, 두 객체의 hashCoder가 서로 다른 값을 반환할 필요는 없다.
단, 다른 객체에 대해서는 다른 값을 반환해야 해시테이블의 성능이 좋아진다.
<br> 좋은 해시 함수라면 서로 다른 인스턴스에 다른 해시코드를 반환한다.
  
두 번째 조항을 조심하자. equals는 물리적으로 다른 두 객체를 논리적으로 같다고 할 수 있다.
하지만 `Object`의 기본 hashCode 메서드는 이 둘이 전혀 다르다고 판단하여 규약과 달리 서로 다른 값을 반환한다.

> ### hashCode의 쓰임
> ```java
> Map<PhoneNumber, String> m = new HashMap<>();
> m.put(new PhoneNumber(707, 867, 5309), "제니");
> 
> m.get(new PhoneNumber(707, 867, 5309), "제니"); // null이 반환된다
> ```
> 두 개의 `PhoneNumber` 인스턴스가 사용되었다. hashCode를 재정의하지 않았기 때문에
> 논리적 동치인 두 객체가 서로 다른 해시코드를 반환하여 get 요청시 `null`이 반환되었다.
> 설령 두 인스턴스를 같은 버킷에 담았다고 하더라도 여전히 `null`을 반환한다.
> `HashMap`은 해시코드가 다른 엔트리끼리는 동치성 비교를 시도조차 하지 않도록 최적화되어있기 때문이다.

해시 충돌이 더욱 적은 방법을 꼭 써야 한다면 구아바의 `Hashing`을 참고하자.

`Objects` 클래스는 임의의 개수만큼 객체를 받아 해시코드를 계산해주는 정적 메서드 hash를 제공한다.
입력 인자를 담기 위한 배열이 만들어지고, 그 중 기본 타입이 있다면 박싱과 언박싱도 거쳐야 하기 때문에
속도는 느린 편이다. 성능에 민감하지 않은 상황에서만 사용하자.
```java
@Override public int hashCode() {
    return Objects.hash(lineNum, prefix, areaCode);    
}
```

성능을 높인답시고 해시코드를 계산할 때 핵심 필드를 생략해서는 안 된다.
속도야 빨라지겠지만, 해시 품질이 나빠져 해시테이블의 성능을 심각하게 떨어뜨릴 수도 있다.

hashCode가 반환하는 값의 생성 교츅을 API 사용자에게 자세히 공표하지 말자.
그래야 클라이언트가 이 값에 의지하지 않게 되고, 추후에 계산 방식을 바꿀 수도 있다.

### Item13 toString을 항상 재정의하라
`Object`의 기본 toString 메서드는 {클래스 이름}@{16진수로 표시한 해시코드}를 반환한다.
toString의 일반 규약에 따르면 간결하면서 사람이 읽기 쉬운 형태의 유익한 정보를 반환해야 한다.
또한 toString의 규약은 모든 하위 클래스에서 이 메서드를 재정의하라고 한다.

실전에서 toString은 그 객체가 가진 주요 정보 모두를 반환하는 게 좋다.
물론 객체가 거대하거나 객체의 상태가 문자열로 표현하기에 부적합하다면 무리가 있으므로
요약 정보를 담아야 한다.

toString이 반환한 값에 포함된 정보를 얻어올 수 있는 API를 제공하자.
예컨대 `PhoneNumber` 클래스는 지역 코드, 프리픽스, 가입자 번호용 접근자를 제공해야 한다.
그렇지 않으면 이 정보가 필요한 프로그래머는 toString의 반환값을 파싱할 수밖에 없다.

정적 유틸리티 클래스와 대부분의 열거 타입은 toString을 따로 재정의하지 않아도 된다.

### Item14 clone 재정의는 주의해서 진행하라
`Cloneable`은 복제해도 되는 클래스임을 명시하는 용도의 mixin interfae지만
의도한 목적을 제대로 이루지 못했다. clone 메서드가 선언된 곳이 `Cloneable`이 아닌 `Object이고,
그마저도 `protected`이기 때문이다. 그래서 `Cloneable`을 구현하는 것만으로는
외부 객체에서 clone 메서드를 호출할 수 없다. 그럼에도 널리 쓰이고 있으므로 알아두는 게 좋다.
이번 아이템에서는 clone 메서드를 잘 동작하게끔 해주는 구현 방법과
언제 그렇게 해야 하는지를 알려주고, 가능한 다른 선택지에 대해 알아보겠다.

일단 `Cloneeable` 사용법만 익히고 그런가보다 하고 넘아간다.

결론: `Cloneable`이 몰고 온 모든 문제를 되짚어봤을 때, 새로운 인터페이스를 만들 때는
절대 `Cloneable`을 확장해서는 안 되며, 새로운 클래스도 이를 구현해서는 안 된다.
복제 기능은 생성자와 팩터리를 이용하는 게 최고다. 단, 배열만은 clone 메서드 방식이 가장 깔끔하다.

## 4장 클래스와 인터페이스

### Item15 클래스와 멤버 접근 권한을 최소화하라
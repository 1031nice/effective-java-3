# effective-java-3
코드를 구성하는 관례적이고 효과적인 방법에 관한 책. **Best Practice**.
그 방법의 방향은 이해하기 쉬운 코드, 수정과 개선이 수월한 코드.
이 책은 총 90개의 아이템을 담았다. 각각의 아이템이 하나의 규칙을 다루며,
각 규칙은 업계 최고의 베테랑 프로그래머들이 유익하다고 인정하는 관례다.

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
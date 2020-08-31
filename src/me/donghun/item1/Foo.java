package me.donghun.item1;

public class Foo {

    String firstName;
    String lastName;
    static final Foo CONSTANT_FOO = new Foo();

    // 생성자
    public Foo(){
    }

    public Foo(String firstName){
        this.firstName = firstName;
    }

    // 위의 생성자와 메소드 시그니처가 같기 때문에 컴파일 에러
//    public Foo(String lastName){
//        this.lastName = lastName;
//    }

    public static Foo getFancyFoo(){
        return new Foo();
    }
    public static Foo getCoolFoo(){
        return new Foo();
    }

    // 생성자와 달리 메소드 이름이 다를 수 있기 때문에 같은 매개변수 타입이어도 공존 가능
    public static Foo getFooWithFirstname(String firstName){
        Foo foo = new Foo();
        foo.firstName = firstName;
        return foo;
    }
    public static Foo getFooWithLastname(String lastName){
        Foo foo = new Foo();
        foo.lastName = lastName;
        return foo;
    }

    public static Foo getWhatEverFoo(){
        return new BabyFoo(); // 하위타입(구현체)를 리턴할 수도 있음
    }

    public static Foo getFooTypeClassFromName(String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class<?> aClass = Class.forName(className);
        Foo foo = (Foo) aClass.newInstance();
        return foo;
    }

    // 생성자와 달리 매번 객체를 생성하지 않을 수도 있음
    public static Foo getConstantFoo(){
        return CONSTANT_FOO;
    }

    public static void main(String[] args) {
        Foo foo1 = Foo.getCoolFoo(); // 이름을 붙일 수 있기 때문에 보다 명시적이다.
        Foo foo2 = Foo.getFancyFoo();
        new Foo();
    }

    static class BabyFoo extends Foo {}
}

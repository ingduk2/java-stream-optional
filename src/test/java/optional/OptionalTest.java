package optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class OptionalTest {
    @Test
    public void create() {
        Optional<String> empty = Optional.empty();
        System.out.println("empty = " + empty);
        assertFalse(empty.isPresent());

        String name = "ingduk2";
        Optional<String> optOf = Optional.of(name);
        System.out.println("optOf = " + optOf);
        assertTrue(optOf.isPresent());

        Optional<String> nullable1 = Optional.ofNullable(null);
        System.out.println("nullable = " + nullable1);
        assertFalse(nullable1.isPresent());

        Optional<String> nullable2 = Optional.ofNullable(name);
        System.out.println("nullable2 = " + nullable2);
        assertTrue(nullable2.isPresent());
    }

    @Test
    public void value_check() {
        Optional<String> optOf = Optional.of("ingduk2");
        assertTrue(optOf.isPresent());

        Optional<String> optOfNullable = Optional.ofNullable(null);
        assertFalse(optOfNullable.isPresent());
        assertTrue(optOfNullable.isEmpty()); //java 11
    }

    @Test
    public void if_present() {
        String name = "ingduk2";
        if (name != null) {
            System.out.println(name.length());
        }

        Optional<String> opt = Optional.ofNullable(name);
        opt.ifPresent(n -> System.out.println(n.length()));

        Optional<String> opt2 = Optional.ofNullable(null);
        opt2.ifPresent(n -> System.out.println(n.length()));
    }

    @Test
    public void orElse() {
        String ret = "";
        String name = null;
        Optional<String> ofNullable = Optional.ofNullable(name);
        ret = ofNullable.orElse("ingduk2");
        assertEquals("ingduk2", ret);

        //값 비교는 이게 더 좋다.
        ret = name != null ? name : "ingduk2";
        assertEquals("ingduk2", ret);
    }

    @Test
    public void orElseGet() {
        String name = null;
        Optional<String> ofNullable = Optional.ofNullable(name);
        name = ofNullable.orElseGet(() -> "ingduk2");
        assertEquals("ingduk2", name);
    }

    @Test
    public void orElseAndorElseGet1() {
        String name = null;
        String text = Optional.ofNullable(name).orElseGet(this::getText);
        assertEquals("ingduk2", text);
        text = Optional.ofNullable(name).orElse(getText());
        assertEquals("ingduk2", text);
    }

    @Test
    public void orElseAndorElseGet2() {
        String name = "ingduk33333";
        String text = Optional.ofNullable(name).orElseGet(this::getText);
        assertEquals("ingduk33333", text);
        //null 이 아니어도 실행됨.
        text = Optional.ofNullable(name).orElse(getText());
        assertEquals("ingduk33333", text);
    }

    public String getText() {
        System.out.println("getText");
        return "ingduk2";
    }

    @Test
    public void orElseThrow() {
        String name = null;
        assertThrows(NullPointerException.class, () -> Optional.ofNullable(name).orElseThrow(NullPointerException::new));
    }

    @Test
    public void get() {
        Optional<String> opt = Optional.of("ingduk2");
        String name = opt.get();
        assertEquals("ingduk2", name);

        //null 일 경우 NoSuchElementException 때문에 사용하면 안좋다.
        Optional<String> optNullable = Optional.ofNullable(null);
//        String name2 = optNullable.get();
        assertThrows(NoSuchElementException.class, () -> optNullable.get());
    }

    @Test
    public void filter() {
        Integer year = 2020;
        Optional<Integer> opt = Optional.ofNullable(year);
        boolean isFalse = opt.filter(y -> y == 2019).isPresent();
        assertFalse(isFalse);
        boolean isTrue = opt.filter(y -> y == 2020).isPresent();
        assertTrue(isTrue);

        //no filter
        Modem modem = new Modem();

        boolean isInRange = false;
        if (modem != null && modem.getPrice() != null && (modem.getPrice() >= 10 && modem.getPrice() <= 15)) {
            isInRange = true;
        }
        assertFalse(isInRange);

        //refactoring with filter
        isInRange = false;
        isInRange = Optional.ofNullable(modem).map(o -> modem.getPrice()).filter(p -> p >= 10).filter(p -> p <= 15).isPresent();
        assertFalse(isInRange);
    }

    public static class Modem{
        private Integer price;

        public Integer getPrice() {
            return price;
        }

        public Modem() {
        }

        public Modem(Integer price) {
            this.price = price;
        }
    }

    @Test
    public void map() {
        List<String> companyNames = Arrays.asList("a", "b", "c", "d");
        Optional<List<String>> listOptional = Optional.of(companyNames);
        int size = listOptional.map(List::size).orElse(0);
        assertEquals(4, size);

        String name = "ingduk2";
        Optional<String> stringOptional = Optional.of(name);
        int length = stringOptional.map(String::length).orElse(0);
        assertEquals(7, length);

        String password = " password    ";
        Optional<String> optionalPwd = Optional.of(password);
        boolean corretPwd = optionalPwd.filter(pass -> pass.equals("password")).isPresent();
        System.out.println(optionalPwd.filter(pass -> pass.equals("passworda"))); //Optional.empty
        System.out.println(corretPwd);
        assertFalse(corretPwd);

        corretPwd = optionalPwd.map(String::trim).filter(pwd -> pwd.equals("password")).isPresent();
        System.out.println(corretPwd);
        assertTrue(corretPwd);
    }


    @Test
    public void flatMap() {
        Person person = new Person("jogn", 26);
        Optional<Person> personOptional = Optional.of(person);
        //map
        Optional<Optional<String>> nameOptionalWrapper = personOptional.map(Person::getName);
        Optional<String> nameOptional = nameOptionalWrapper.orElseThrow(IllegalArgumentException::new);
        String name1 = nameOptional.orElse("");
        assertEquals("jogn", name1);

        //flatmap
        String name2 = personOptional.flatMap(Person::getName).orElse("");
        assertEquals("jogn", name2);
    }


    public static class Person {
        private String name;
        private int age;
        private String password;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public Optional<String> getName() {
            return Optional.ofNullable(name);
        }

        public Optional<Integer> getAge() {
            return Optional.ofNullable(age);
        }

        public Optional<String> getPassword() {
            return Optional.ofNullable(password);
        }
    }


}

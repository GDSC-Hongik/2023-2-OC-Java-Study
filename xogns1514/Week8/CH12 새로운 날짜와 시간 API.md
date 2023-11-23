# CH12 새로운 날짜와 시간 API

---

자바 1.0에서는 [java.util.Date](http://java.util.Date) 클래스 하나로 날짜와 시간 관련 기능을 제공했다. Date 클래스는 특정 시점을 날짜가 아닌 밀리초 단위로 표현했다. 게다가 1900년을 기준으로 하는 오프셋, 0에서 시작하는 달 인덱스 등 모호한 설계로 유용성이 떨어졌다. 

이를 대안으로 java.util.Calendar라는 클래스를 대안으로 제공했다. 이 역시 쉽게 에러를 일으키는 설계 문제를 갖고있었다. 둘다 가변 클래스로 유지보수에 어려움이 있었다.

따라서 자바 8에서는 java.time 패키지를 제공한다

## 12.1. LocalDate, LocalTime, Instant, Duration, Period 클래스

## 12.1.1 LocalDate와 LocalTime 사용

```java
LocalDate date = LocalDate.of(2017, 9, 21); // 2017-09-21
int year = date.getYear(); // 2017
Month month = date.getMonth(); // SEPTEMBER
int day = date.getDayOfMonth; // 21
DayOfWeek dow = date.getDayOfWeek(); // THURSDAY
int len = date.lengthOfMonth(); // 31
boolean leap = date.isLeapYear(); //false(윤년 아님)
///
LocalDate today = LocalDate.now(); // 현재 날짜 정보를 얻는다.
```

- LocalDate 인스턴스는 시간을 제외한 날짜를 표현하는 불변 객체다. 어떤 시간대 정보도 포함하지 않는다.
- 정적 팩토리 메서드 of로 LocalDate 인스턴스를 만들 수 있다.

```java
// TemporalField를 이용해서 LocalDate 값 읽기
int year = date.get(ChronoField.YEAR);
int month = date.get(ChronoField.MONTH_OF_YEAR);
int day = date.get(ChronoField.DAY_OF_MONTH);
```

- TemporalField: 시간 관련 객체에서 어떤 필드의 값에 접근할지 정의하는 인터페이스
- ChronoField: TemporalField 인터페이스 정의

```java
// LocalTime 만들고 값 읽기
LocalTime time = LocalTime.of(13, 45 ,20); // 13:45:20
int hour = time.getHour(); // 13
int minute = time.getMinute(); // 45
int second = time.getSecond(); // 20
// parse 정적 메서드 이용
LocalDate date = LocalDate.parse("2017-09-21");
LocalTime time = LocalTime.parse("13:45:20");
```

- parse 메서드에 DateTimeFormatter를 전달할 수 있다.
- DateTimeFormatter의 인스턴스는 날짜, 시간 객체의 형식을 지정한다.

## 12.1.2 날짜와 시간 조합

```java
// 2017-09-21T13:45:20
LocalDateTime dt1 = LocalDateTime.of(2017, Month.SEPTEMBER, 21 ,13, 45, 20);
LocalDateTime dt2 = LocalDateTime.of(date, time);
LocalDateTime dt3 = date.atTime(13, 45, 20);
LocalDateTime dt4 = date.atTime(time);
LocalDateTime dt5 = time.atDate(date);
// 추출
LocalDate date1 = dt1.toLocalDate(); // 2017-09-21
LocalTime time1 = dt1.toLocalTime(); // 13:45:20
```

- LocalDateTime은 LocalDate와 LocalTime을 쌍으로 갖는 복합 클래스이다.
- LocalDate의 atTime 메서드에 시간을 제공하거나 LocalTime의 atDate 메서드에 날짜를 제공해서 만들 수 있다.
- LocalDateTime의 toLocalDate나 toLocalTime 메서드로 LocalDate나 LocalTime 인스턴스를 추출할 수 있다.

## 12.1.3 Instant 클래스: 기계의 날짜와 시간

```java
Instant.ofEpochSecond(3);
Instant.ofEpochSecond(3, 0);
Instant.ofEpochSecond(2, 1_000_000_000); // 2초 이후의 1억 나노초(1초)
Instant.ofEpochSecond(4, -1_000_000_000); // 4초 이전의 1억 나노초(1초)
```

- java.time.Instant 클래스에서는 기계적인 관점에서 시간을 표현한다.
- Instant 클래스는 유닉스 에포크 시간(1970년 1월 1일 0시 0분 0초 UTC)을 기준으로 특정 지점까지의 시간을 초로 표현한다.

```java
int day = Instant.now().get(ChronoField.DAY_OF_MONTH);
// 위 코드는 다음 에러를 일으킨다.
// java.time.temporal.UnsupportedTemporalTypeException: Unsupported field: DayOfMonth
```

- Instant는 사람이 읽을 수 있는 시간 정보를 제공하지 않는다.

## 12.1.4 Duration과 Period 정의

```java
Duration threeMinutes = Duration.ofMinutes(3);
Duration threeMinutes = Duration.of(3, ChronoUnit.MINUTES);

Period tenDays = Period.ofDays(10);
Period threeWeeks = Period.ofWeeks(3);
Period twoYearsSixMonthsOneDay = Period.of(2, 6, 1);
```

- Duration 클래스의 정적 팩토리 메서드 between으로 두 시간 객체 사이의 지속시간을 만들 수 있다.
- LocalDateTime은 사람이 사용하도록, Instant는 기계가 사용하도록 만들어진 클래스로 두 인스턴스는 혼합할 수 없다.
- 년, 월, 일로 시간을 표현할 때는 Period 클래스를 사용한다.
- Period 클래스의 팩토리 메서드 between을 이용하면 두 LocalDate의 차이를 확인할 수 없다

## 12.2 날짜 조정, 파싱, 포매팅

```java
// 절대적인 방식으로 LocalDate의 속성 바꾸기
LocalDate date1 = LocalDate.of(2017, 9 , 21); // 2017-09-21
LocalDate date2 = date1.withYear(2011); // 2011-09-21
LocalDate date3 = date2.withDayOfMonth(25); // 2011-09-25
LocalDate date4 = date3.with(ChronoField.MONTH_OF_THE_YEAR, 2); // 2011-02-25 
```

- withAttribute 메서드로 기존의 LocalDate를 바꾼 버전을 만들 수 있다.
- 바뀐 속성을 포함하는 새로운 객체를 반환한다.

```java
// 상대적인 방식으로 LocalDate 속성 바꾸기
LocalDate date1 = LocalDate.of(2017, 9 ,21); // 2017-09-21
LocalDate date2 = date1.plusWeek(1); // 2017-09-28
LocalDate date3 = date2.minusYears(6); // 2011-09-28
LocalDate date4 = date3.plus(6, ChronoUnit.MONTHS); // 2012-03-28
```

- plus, minus 메서드를 이용해 Temporal을 특정 시간만큼 앞뒤로 이동시킬 수 있다.

## 12.2.1 TemporalAdjusters 사용하기

```java
// 미리 정의된 TemporalAdjusters 사용하기
import static java.time.temporal.TemporalAdjusters.*;
LocalDate date1 = LocalDate.of(2014, 3, 18); // 2014-03-18
LocalDate date2 = date1.with(nextOrSame(DayOfWeek.SUNDAY)); // 2014-03-23
LocalDate date3 = date2.with(lastDayOfMonth()); // 2014-03-31
```

- 복잡한 날짜 조정을 할때,  더 다양한 동작을 수행할 수 있도록 하는 기능을 제공하는 TemporalAdjuster를 with 메서드에 전달하는 방법을 사용할 수 있다.

```java
// TemporalAdjuster 인터페이스
@FunctionalInterface
public interface TemporalAdjuster {
	Temporal adjustInto(Temporal temporal);
}
```

- 필요한 기능은 TemporalAdjuster를 구현하여 custom TemporalAdjuster를 구현할 수 있다.

## 12.2.2 날짜와 시간 객체 출력과 파싱

날짜와 시간 관련 작업에서 포매팅과 파싱 전용 패키지인 java.time.format이 새로 추가되었다.

```java
// 날짜 객체 문자열로
LocalDate date = LocalDate.of(2014, 3, 18);
String s1 = date.format(DateTimeFormatter.BASIC_ISO_DATE); // 20140318
String s2 = date.format(DateTimeFormatter.ISO_LOCAL_DATE); // 2014-03-18
// 문자열 날짜 객체로
LocalDate date1 = LocalDate.parse("20140318", DateTimeFormatter.BASIC_ISO_DATE);
```

- DateTimeFormatter를 이용해서 날짜나 시간을 특정 형식의 문자열로 만들 수 있다.
- java.util.DateFormat 클래스와 달리 스레드에서 안전하게 사용할 수 있는 클래스이다.
- 반대로 날짜나 시간을 표현하는 문자열을 파싱해서 날짜 객체를 다시 만들 수 있다.

```java
// 패턴으로 DateTimeFormatter 만들기
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
LocalDate date1 = LocalDate.of(2014, 3, 18);
String formatterdDate = date1.format(formatter);
LocalDate date2 = LocalDate.parse(formattedDate, formatter);
```

- DateTimeFormmatter 클래스는 특정 패턴으로 포매터를 만들 수 있는 정적 팩토리 메서드도 제공한다.

```java
// 지역화된 DateTimeFormmater 만들기
DateTimeFormatter italianFormatter = 
	DateTimeFormatter.ofPattern("d. MMMM yyyy", Locale.ITALIAN);
LocalDate date1 = LocalDate.of(2014, 3, 18);
String formattedDate = date.format(italianFormmter); // 18. marzo 2014
LocalDate date2 = LocalDate.parse(formattedDate, italianFormatter);
```

- ofPattern 메서드도 Locale로 포매터를 만들 수 있도록 오버로드된 메서드를 제공한다.

```java
// DateTimeFormatter 만들기
DateTimeFormatter italianFormatter = new DateTimeFormatterBuilder()
        .appendText(ChronoField.DAY_OF_MONTH)
        .appendLiteral(". ")
        .appendText(ChronoField.MONTH_OF_YEAR)
        .appendLiteral(" ")
        .appendText(ChronoField.YEAR)
        .parseCaseInsensitive()
        .toFormatter(Locale.ITALIAN);
```

## 12.3 다양한 시간대와 캘린더 활용법

- 기존의 java.util.TimeZone을 대체할 수 있는 java.util.ZoneId 클래스가 새롭게 등장했다.
- ZoneId 클래스를 이용하면 서머타임 같은 복잡한 사항이 자동으로 처리된다.
- ZoneId는 불변 클래스이다.

## 12.3.1 시간대 사용하기

- 표쥰 시간이 같은 지역을 묶어서 시간대 규칙 집합을 정의한다.
- ZoneId romeZone = ZoneId.of(”Europe/Rome”); → ‘{지역}/{도시}’
- ZoneId 객체를 언등ㄴ 다음 LocalDate, LocalDateTime, Instant를 이용해 ZonedDateTime 인스턴스로 변환할 수 있다.

```java
// 특정 시점에 시간대 적용
LocalDate date = LocalDate.of(2014, Month.MARCH, 18);
ZonedDateTime zdt1 = date.atStartOfDay(romeZone);
LocalDateTime dateTime = LocalDateTime.of(2014, Month.MARCH, 18, 13, 45);
ZonedDateTime zdt2 = dateTime.atZone(romeZone);
Instant instant = Instant.now();
ZonedDateTime zdt3 = instant.atZone(romeZone);
```
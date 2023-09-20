# WIL6 - 7장

지금까지는 스트림을 통해 컬렉션을 선언형으로 제어하는 방법을 살펴봤다.

이번 장에선 `병렬 데이터 처리와 성능`에 대해 다룰 계획이다.

## **병렬 스트림**

앞서, 4장에선 스트림 인터페이스를 이용하면 아주 간단하게 요소를 병렬로 처리할 수 있다고 이야기했다. `병렬 스트림`이란 각각의 스레드에서 처리할 수 있도록 스트림 요소를 여러 청크로 분할한 스트림이다.

여러 청크로 분할되어있으니 모든 멀티코어 프로세서가 각각의 청크를 할당받아 처리할 수 있다.

숫자 n을 인수로 받아 1~n까지 모든 숫자의 합을 반환하는 ㅁ네서드를 구현할때 우선 스트림으로 구현하면 다음과 같이 무한스트림을 받아 크기를 제한하는 방식으로 구현할 수 있다.

```java
  public static long sequentialSum(long n) {
    return Stream.iterate(1L, i -> i + 1)
    			.limit(n)
                .reduce(Long::sum)
                .get();
  }
```

이때 n이 커진다면 병렬로 처리하는 것이 좋은데 결과 변수 동기화, 스레드 개수, 숫자 생성 여부 등 고려해야할 요소가 있다.

### **순차스트림을 병렬 스트림으로 변환하기**

```java
public static long parallelSum(long n) {
  return Stream.iterate(1L, i -> i + 1)
  			.limit(n)
            .parallel()
            .reduce(0L,Long::sum);
}
```

순차 스트림에 parallel 메서드를 호출하면 기존 스트림의 연산을 병렬로 처리할 수 있다. 위 코드가 이전 코드와 다른점은 스트림을 여러 청크로 분할되어 있다는 것이다. 아래 그림과 같이, 리듀싱 연산을 여러 청크로 나누어 `병렬`로 수행할 수 있다.

![https://blog.kakaocdn.net/dn/AzyDL/btsuZU98abz/rhmAiK0j4wTgWUGHu2rSs1/img.png](https://blog.kakaocdn.net/dn/AzyDL/btsuZU98abz/rhmAiK0j4wTgWUGHu2rSs1/img.png)

`parallel()`을 호출해도 스트림 자체에는 어떠한 변화도 일어나지 않는다. 다만, 내부에서 병렬 수행을 의미하는 플래그가 설정된다. 반대로 `sequential()`을 설정해 병렬 스트림을 순차 스트림으로 바꿀 수 있으며 마지막에 호출된 메서드가 전체 파이프라인에 영향을 끼친다.

### **스트림 성능 측정**

일반적으로 병렬을 사용하면 순차 스트림보다 성능이 더 좋아질 것이라고 판단하는데 진짜일까? 추측말고 직접 측정을 해보자. 아래 2개의 의존성을 추가해 성능 측정을 위한 JMH를 사용할 수 있다.

```java
    implementation 'org.openjdk.jmh:jmh-core:1.21'
    implementation 'org.openjdk.jmh:jmh-generator-annprocess:1.21'
```

첫번째는 핵심 JMH 구현을 포함하고 두번째 라이브러리는 JAR파일을 만드는데 필요한 어노테이션 프로세서를 포함한다.

아무튼, 아래 코드를 작성 후 `java -jar ./target/benchmarks.jar ParallelStreamBenchmark` 명령어를 통해 성능 측정을 해보자.

```java
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(value = 2,jvmArgs = {"-Xms4G", "-Xmx4G"})
public class ParallelStreamBenchmark {
    private static final long N=10_000_000L;

    @Benchmark
    public long sequentialSum(){
        return Stream.iterate(1L,i->i+1)
                .limit(N)
                .reduce(0L,Long::sum);
    }

    @TearDown(Level.Invocation)
    public void tearDown(){
        System.gc();
    }
}
```

**@Benchmark:**  해당 메서드의 성능을 측정한다는 의미.

이 외에도 for루프를 사용하는 저수준 방법의 iterativeSum, parallelSum등을 같이 벤치마킹해 성능 측정을 해보면 다음과 같다.

![https://blog.kakaocdn.net/dn/s1Cqp/btsu19lpBno/FsU6wKos8eRgTrMCRQb0g1/img.png](https://blog.kakaocdn.net/dn/s1Cqp/btsu19lpBno/FsU6wKos8eRgTrMCRQb0g1/img.png)

이 결과를 보면, 순차 스트림을 사용하는 버전에 비해 저수준 반복이 더 빠르고, 순차스트림이 병렬스트림보다 더 빠르다는 것을 알 수 있다. `iterate`연산은 리듀싱 연산이 수행되지 않기에 전체 숫자리스트가 준비되어있지 않아 스트림을 병렬로 처리하기 위한 청크로 분할하기가 어렵다. 따라서, 순차스트림과 큰 차이가 없고 오히려, 스레드를 할당하는 오버헤드가 증가하게되어 성능이 저하된 것이다.

따라서, parallel 메서드를 호출했을때, 내부적으로 일어나는 일에 대해 꼭 이해해야한다.

### **더 특화된 메서드 사용**

멀티코어 프로세서를 효과적으로 활용하기 위해선, 더 특화된 메서드를 사용하는 것이 좋다. 가령 LongStream의 rangeClosed라는 메서드는 ietrate에 비해 다음과 같은 장점을 가지고 있다.

- 기본형 long을 직접 사용하므로 박싱, 언박싱 오버헤드가 사라짐
- 쉽게 청크로 분할할 수 있는 숫자 범위를 생산함.

```java
@Benchmark
  public long rangedSum() {
    return LongStream.rangeClosed(1, N)
    			.reduce(0L, Long::sum);
  }
```

이 오버헤드를 비교하기 위해 위 rangedSum코드를 넣고 다시 아래의 결과 사진을 보자.

![https://blog.kakaocdn.net/dn/s1Cqp/btsu19lpBno/FsU6wKos8eRgTrMCRQb0g1/img.png](https://blog.kakaocdn.net/dn/s1Cqp/btsu19lpBno/FsU6wKos8eRgTrMCRQb0g1/img.png)

rangedSum이 iterate를 사용했을때인 sequentialSum보다 확실히 더 빠르다. 즉, 박싱, 언박싱 오버헤드만 줄여도 큰 성능 이득을 볼 수 있다는 것이다.

추가적으로 parallelSum()을 아래와 같이  rangeClosed를 활용해 성능을 비교하면 더 나은 성능을 얻을 수 있다.

```java
@Benchmark
  public long parallelRangedSum() {
    return LongStream.rangeClosed(1, N)
    			.parallel()
                .reduce(0L, Long::sum);
  }
```

### **병렬 스트림의 올바른 사용법**

병렬 스트림을 사용할때는 공유된 상태를 바꾸는 과정에 유의해야한다. 다음과 같이 특정 값을 누적해 초기화하느고 숫자를 코드하는 코드를 보자.

```java
    public static long sideEffectSum(long n){
        Accumulator accumulator = new Accumulator();
        LongStream.rangeClosed(1,n).forEach(accumulator::add);
        return accumulator.total;
    }

    public class Accumulator {
    	public long total = 0;
    	public void add(long value) {
        	total+=value;
    	}

    	public Accumulator() {
    	}
    }
```

일반적으론, 문제가 없어보이지만 해당 코드를 병렬로 실행하게되면 바로 문제를 알 수 있다.

```java
    public static long sideEffectParallelSum(long n) {
        Accumulator accumulator = new Accumulator();
        LongStream.rangeClosed(1, n).parallel().forEach(accumulator::add);
        return accumulator.total;
    }
```

이 코드를 한줄한줄 실행하면 정상적인 값 자체가 나오지 않는다. 여러 스레드가 동시에 `total+=value`를 실행하기에 이런 문제가 발생하는 것이다.

따라서, 병렬 스트림에선 공유된 가변 변수를 사용하는 것을 피해야한다.

### **병렬 스트림을 효과적으로 사용하기**

병렬스트림을 사용하기 위한 기준들을 생각해보자.

- 확신이 서지 않으면 직접 벤치마크를 통한 성능 측정으로 순차스트림과 병렬 스트림을 결정하자.
- 박싱과 언박싱은 성능을 저하시킬 수 있다. 따라서, 되도록 기본형 특화 스트림을 사용하자.
- 요소의 순서에 의존하는 연산은 병렬스트림에서 비싼 비용을 치러야한다. `findAny`, `findFirst`등을 사용할때 주의하자.
- 전체 파이프라인 연산 비용을 고려하자. 처리할 요소가 N이고 하나를 처리할때 Q만큼의 비용이 들면 N*Q의 비용을 이야기한다. 이때 Q가 높아질수록 병렬 스트림을 사용하는 것이 유리할 것.
- 소량의 데이터는 병렬 처리하지 말자!
- 스트림을 구성하는 자료구조의 적절성을 확인하자. ArrayList가 LinkedList보다 분할하기 유리하다.
- 중간 연산이 스트림의 특성을 어떻게 바꾸는지에 따라 분해 과정의 성능이 달라질 수 있다.
- 최종 연산의 병합 비용을 고려하자. 병합 비용이 비싸면 병렬로 얻은 이득을 그대로 상회할 수 있다.

![https://blog.kakaocdn.net/dn/JiGJW/btsuShSmG7e/HnfQ18GvbvxjHtwWbpumDK/img.png](https://blog.kakaocdn.net/dn/JiGJW/btsuShSmG7e/HnfQ18GvbvxjHtwWbpumDK/img.png)

---

## **포크/조인 프레임워크**

자바7에서 병렬 처리를 하기위해선 포크/조인 프레임워크를 통해 병렬화 작업을 재귀적으로 분할 후, 서브태스크의 결과를 합쳐 전체 결과를 만드는 방식으로 설계했다. 이 프레임워크는 서브태스크를 `스레드 풀(ForkJoinPoll)`의 작업자 스레드에 분산할당하는 인터페이스 `ExecutorService`를 구현한다.

### **RecursiveTask 활용**

스레드 풀을 활용하기 위해선 `RecursiveTask<R>`의 서브클래스를 만들어야한다. 여기서 R은 결과 형식 또는 결과가 없을땐 RcursiveAction 의 형식이다. 이를 정의하려면 추상메서드 compute를 정의해야한다.

compute 메서드는 테스크를 서브테스크로 분할하는 로직과, 더이상 분할이 불가능할때 개별 서브테스크의 결과를 생산할 알고리즘을 정의한다. 이 과정은 분할/정복 기법과 유사하다.

![https://blog.kakaocdn.net/dn/I34hO/btsuIDa7bD0/ClzXy8gqJDFGq8eeUrKld0/img.png](https://blog.kakaocdn.net/dn/I34hO/btsuIDa7bD0/ClzXy8gqJDFGq8eeUrKld0/img.png)

따라서, 아래 코드와 같이 RecursiveTask를 상속받아 compute 추상 메서드를 구현해야한다.

```java
public ForkJoinSumCaculator extends RecursiveTask<Long>{

	@Override
    protected Long compute(){
//...
    }
}
```

교재에서 제공한 실제 코드도 분할정복과 상당히 유사하다.

### **ForkJoinSumCaculator 실행**

ForkJoinSumCaculator를 ForkJOinPoll로 전달하면 스레드가 compute메서드를 실행하며 작업을 수행한다. compute 메서드는 병렬로 실행할만큼 크기가 작아졌는지 확인해 충분히 작아졌다면,역순으로 트리를 방문해 부분 결과를 합쳐 최종 결과를 도출한다. 아래 그림을 참조하자.

![https://blog.kakaocdn.net/dn/cuNZQj/btsuTNQ2bmH/1NQabdUOKo14CQ0tux5D6k/img.png](https://blog.kakaocdn.net/dn/cuNZQj/btsuTNQ2bmH/1NQabdUOKo14CQ0tux5D6k/img.png)

### **포크/조인 프레임워크를 제대로 사용하는 방법**

해당 프레임 워크를 효과적으로 사용하기 위해선 다음과 같은 사항을 유의해야한다.

- join 메서드를 태스크에 호출하면 태스크가 생산하는 결과가 준비될 때까지 호출자를 블록한다. 따라서, 두 서브태스크가 모두 시작된 다음 join을 호출하자.
- RecursiveTask 내에서는 ForkJoinPool의 invoke메서드를 사용하지 말자. 그 대신 compute, fork를 직접 호출하고 invoke는 순차코드에서 병렬 계산을 시작할때만 사용한다.
- 서브태스크에 fork 메서드를 호출해 ForkJoinPool의 일정을 조정할 수 있다.
- 포크/조인 프레임워크로 디버깅은 스택 트레이스로 도움을 얻을 수 없어 디버깅하기가 어렵다.
- 포크/조인 프레임워크를 사용하는것이 무조건 빠르진 않다. (앞서 병렬도 무조건 빠른건 아니었잖아!)

### **작업 훔치기**

분할한 태스크를 공정하게 나누면 좋지만, 실제에선 각각의 태스크가 같은 시간에 종료되기는 어렵다. 포크/조인 프레임워크에서는 `작업 훔치기`라는 기법으로 이 문제를 해결해 모든 스레드를 공정하게 분할한다.

할당된 태스크를 이중 연결리스트에 기록해 참조하며 작업을 진행하고 작업이 끝날때마다 큐의 헤드에서 다른 태스크를 가져와 작업을 진행한다. 이때 할일이 끝난 스레드는 다른 스레드 큐의 꼬리에서 작업을 훔쳐와 작업 부하를 비슷한 수준으로 유지한다. 아래 그림을 참조하자.

![https://blog.kakaocdn.net/dn/zsyBt/btsuWYZf6Su/xa1AgkBOKCJ5v14dSk8nb0/img.png](https://blog.kakaocdn.net/dn/zsyBt/btsuWYZf6Su/xa1AgkBOKCJ5v14dSk8nb0/img.png)

---

## **Spliterator 인터페이스**

자바8은 분할할 수 있는 반복자로 Spliterator라는 인터페이스를 제공한다. 일반적인 Iterator처럼 소스의 요소 탐색 기능을 제공하면서도 병렬작업에 특화되어있다.

Spliterator 인터페이스는 다음과 같은 메서드를 정의한다.

`tryAdvance`: 탐색해야할 요소가 남아있으면 true를 반환한다.

`trySplit`: 자신이 반환한 일부 요소를 재분할해서 두번째 Spliterator를 생성한다.

`estimateSize`: 탐색해야할 요소 수 정보를 제공한다.

`characteristics`: 해당 인터페이스가 가진 특징을 비트플래그로 나타낸다. 이 특성들을 참고해 Spliterator를 제어하고 최적화한다.

**ORDERED, DISTINCT, SORTED, SIZED, NON-NULL, IMMUTABLE, CONCURRENT, SUBSIZED**와 같은 특징들이 있다.

### **분할 과정**

이 과정은 아래 그림과 같이 재귀적으로 `trySplit`을 호출하며 일어난다. 반환값이 null이 될때까지 반복하며 모든 결과가 null이면 분할을 종료한다.

![https://blog.kakaocdn.net/dn/6nGHr/btsuQRzBimF/OxkOkW1Cca1xti9bQO9Mp1/img.png](https://blog.kakaocdn.net/dn/6nGHr/btsuQRzBimF/OxkOkW1Cca1xti9bQO9Mp1/img.png)

### **커스텀 Spliterator 구현하기**

문자열의 단어수를 계산하는 단순 메서드르 구현해보자.

```java
  public static int countWordsIteratively(String s) {
    int counter = 0;
    boolean lastSpace = true;
    for (char c : s.toCharArray()) {
      if (Character.isWhitespace(c)) {
        lastSpace = true;
      }
      else {
        if (lastSpace) {
          counter++;
        }
        lastSpace = Character.isWhitespace(c);
      }
    }
    return counter;
  }
```

```java
  public static final String SENTENCE =
      " Nel   mezzo del cammin  di nostra  vita "
      + "mi  ritrovai in una  selva oscura"
      + " che la  dritta via era   smarrita ";
```

다음과 같은 첫문장을 실행하면 19개의 단어를 찾은 결과를 반환한다. 단어 사이의 공백이 여러개여도 정상작동이된다.

이렇게 반복형이 아닌 함수형으로도 단어 수를 세는 메서드를 구현해보자. 우선, String -> Stream으로 변환해 스트림의 reducing 연산을 활용해 단어수를 계산할 수 있다. 단어수를 계산하는 int와 마지막 공백여부를 확인하는 boolean 변수를 두어 공백이 아닌 문자를 만났을때 이전 문자가 공백이었다면 단어개수를 증가시키는 방식으로 클래스를 작성하면 다음과 같다.

```java
private static class WordCounter {

    private final int counter;
    private final boolean lastSpace;

    public WordCounter(int counter, boolean lastSpace) {
      this.counter = counter;
      this.lastSpace = lastSpace;
    }

    public WordCounter accumulate(Character c) {
      if (Character.isWhitespace(c)) {
        return lastSpace ? this : new WordCounter(counter, true);
      }
      else {
        return lastSpace ? new WordCounter(counter + 1, false) : this;
      }
    }

    public WordCounter combine(WordCounter wordCounter) {
      return new WordCounter(counter + wordCounter.counter, wordCounter.lastSpace);
    }

    public int getCounter() {
      return counter;
    }

  }
```

이를 스트림으로 활용해 아래와 같이 리듀싱 연산을 사용해 구현할 수 있다.

```java
private int countWords(Stream<Character> stream){
    WordCounter wordCounter = stream.reduce(new WordCounter(o,true),
                                                WordCounter::accumulate,
                                                WrodCounter::combine);
    return wordCounter.getCounter();
}
```

**이제 이 WordCounter를 병렬로 수행해보자.**

### **WordCounter를 병렬로 수행하기**

우선, 해당 단어를 바로 병렬로 처리하면 어떨까?

```java
countWords(stream.parallel())
```

위와 같이 실행한 결과를 출력하면 원하는 결과가 나오지 않는데 그 이유는 임의로 분할을 하기때문에 분할 지점에 따라 하나의 단어가 둘로 나뉘는 문제가 발생한다.

따라서, 이 문제를 해결하기 위해 **임의로 분할하는 것이 아닌 단어가 끝나는 위치에서만 분할**하는 방법을 사용해야한다.

```java
    private static class WordCounterSpliterator implements Spliterator<Character> {

    private final String string;
    private int currentChar = 0;

    private WordCounterSpliterator(String string) {
      this.string = string;
    }

    @Override
    public boolean tryAdvance(Consumer<? super Character> action) {
      action.accept(string.charAt(currentChar++));
      return currentChar < string.length();
    }

    @Override
    public Spliterator<Character> trySplit() {
      int currentSize = string.length() - currentChar;
      if (currentSize < 10) {
        return null;
      }
      for (int splitPos = currentSize / 2 + currentChar; splitPos < string.length(); splitPos++) {
        if (Character.isWhitespace(string.charAt(splitPos))) {
          Spliterator<Character> spliterator = new WordCounterSpliterator(string.substring(currentChar, splitPos));
          currentChar = splitPos;
          return spliterator;
        }
      }
      return null;
    }

    @Override
    public long estimateSize() {
      return string.length() - currentChar;
    }

    @Override
    public int characteristics() {
      return ORDERED + SIZED + SUBSIZED + NONNULL + IMMUTABLE;
    }

  }
```

**tryAdvanced:** 문자열에서 현재 인덱스에 해당하는 문자를 Consumer에게 제공후 인덱스를 증가시킨다.

**trySplit:** 반복될 자료구조를 분할하는 로직을 포함한다. 우선, 분할 동작을 중단할 한계를 설정해야한다. 여기선 10개의 문자로 사용했지만 실제론 더 높은 값을 설정해야하며, 한계값 이하일경우 null을 반환시켜 분할을 중지해야한다. 분할할 위치를 찾았다면, Spliterator를 새로만들고 다시 문자를 탐색한다.

estimateSize: 탐색할 요소의 개수는 파싱할 문자열 전체 길이와 현재 위치의 차이이다.

characteristic: 다음과 같은 특성을 다 포함함

이후, 병렬스트림을 countWords로 전달하면 정상적인 결과를 출력할 수 있다.
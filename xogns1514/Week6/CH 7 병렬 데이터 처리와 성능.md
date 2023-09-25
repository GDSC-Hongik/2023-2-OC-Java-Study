# CH 7 병렬 데이터 처리와 성능

## 7.1 병렬 스트림

병렬 스트림: 각각의 스레드에서 처리할 수 있도록 스트림 요소를 여러 청크로 분할한 스트림

- 컬렉션에서 parallelStream을 호출하면 병렬 스트림(parallel stream)이 생성된다.

```java
// 두 숫자를 더하는 BinaryOperation을 통한 리듀싱 작업
public long sequentialSum(long n) {
	return Stream.iterate(1L, i -> i+1) //무한 자연스 스트림 생성
		.limit(n) //n개 이하로 제한
		.reduce(0L, Long::sum); //모든 숫자를 더하는 스트림 리듀싱 연산
}
```

```java
// 반복문을 이용한 구현
public long iterativeSum(long n) {
	long result = 0;
	for(long i = 1L; i <= n; i++){
		result += i;
	}
	return result;
}
```

- n이 커진다면 이 연산을 병렬로 처리하는 것이 좋을 것이다. 병렬 스트림을 이용하면 해결할 수 있다.

## 7.1.1 순차 스트림을 병렬 스트림으로 변환하기

```java
public long parallelSum(long n) {
	return Stream.iterate(1L, i -> i + 1)
		.limit(n)
		.parallel() // 스트림을 병렬 스트림으로 변환
		.reduce(0L, Long::sum);
}
```

- 순차 스트림에 parallel 메서드를 호출하면 기존의 함수형 리듀싱 연산이 병렬로 처리된다.

![0](https://github.com/GDSC-Hongik/2023-2-OC-Java-Study/assets/66353672/77e268c3-e554-434d-96ce-998c20c6d420)


- 스트림이 여러 청크로 분할되어 있다.

→ 리듀싱 연산을 여러 청크에 병렬로 수행한 후 리듀싱 연산으로 생성된 부분 결과를 다시 리듀싱 연산으로 합쳐 전체 스트림의 리듀싱 결과를 도출한다.

- 내부적으로 parallel을 호출하면 이후 연산이 병렬로 수행해야 함을 의미하는 불리언 플래그가 설정된다.

## 7.1.2 스트림 성능 측정

반복혀으 순차 리듀싱, 병렬 리듀싱 방법으로 실행하는 것중 어느 것이 가장 빠른지 확인해보자

📌 성능을 최적화할 때는 측정을 해야한다!

자바 마이크로벤치마크 하니스(JMH)라는 라이브러리를 이용해 작은 벤치마크를 구현한다.

```java
// n개의 숫자를 더하는 함수의 성능 측정
@BenchmarkMode(Mode.AverageTime) // 벤치마크 대상 메서드를 실행하는 데 걸리 평균시간 측정
@OutputTimeUnit(TimeUnit.MILLISECONDS) // 벤치마크 결과를 밀리초 단위로 출력
@Fork(2, jvmArgs={"-Xms4G", "-Xms4G"}) // 4Gb의 힙 공간을 제공한 환경에서 두 번 벤치마크를 수햏해 결과의 신뢰성 확보
public class ParallelStreamBenchmark {
	private static final long N = 10_100_000L;

	@Benchmark // 벤치마크 대상 메서드
	public long sequentialSum() {
		return Stream.iterate(1L, i -> i + 1)
			.limit(N)
			.reduce(0L, Long::sum);
	}

	@Benchmark 
	public long iterativeSum(long n) {
		long result = 0;
		for(long i = 1L; i <= n; i++){
			result += i;
		}
		return result;
	}

	@Benchmark 
	public long parallelSum(long n) {
		return Stream.iterate(1L, i -> i + 1)
			.limit(n)
			.parallel() // 스트림을 병렬 스트림으로 변환
			.reduce(0L, Long::sum);
	}

	@TearDown(Level.Invocation) // 매 번 벤치마크를 실행한 다음에는 가비지 컬렉터 동작 시도
	public void tearDown() {
		System.gc();
	}
}
```

- 클래스를 컴파일하면 메이븐 플러그인이 benchmarks.jar 파일을 만듦

→ java -jar ./target/benchmarks.jar ParallelStreamBenchmark로 실행 가능

- 결과: 병렬 버전이 순차 버전에 비해 다섯 배나 느린 결과가 나왔다.
- 문제점
    - 반복 결과로 박싱된 객체가 만들어지므로 숫자를 더하려면 언박싱을 해야한다.
    - 반복 작업은 병렬로 수행할 수 있는 독립 단위로 나누기가 어렵다.

![1](https://github.com/GDSC-Hongik/2023-2-OC-Java-Study/assets/66353672/f9577644-0cfc-483b-8ece-2a66cab1f5a4)


- 이전 연산의 결과에 따라 다음 함수의 입력이 달라지기 때문에 iterate 연산을 청크로 분할하기가 어렵다.
- 리듀싱 과정을 시작하는 시점에 전체 숫자 리스트가 준비되지 않았으므로 스트림을 병렬로 처리할 수 있도록 청크로 분할할 수 없다.

## 더 특화된 메서드 사용

LongStream.rangeClosed 가 iterate에 비해 가지는 장점

- LongStream.rangeClosed는 기본형 long을 직접 사용하므로 박싱과 언박싱 오버헤드가 사라진다.
- LongStream.rangeClosed는 쉽게 청크로 분할할 수 있는 숫자 범위를 생산한다. 예를 들어 1-20 범위의 숫자를 각각 1-5, 6-10, 11-15, 16-20 범위의 숫자로 분할할 수 있다.

❗️ 올바른 자료구조를 선택해야 병렬 실행도 최적의 성능을 발휘할 수 있다는 사실을 확인할 수 있다.

## 7.1.3 병렬 스트림의 올바른 사용법

병렬 스트림을 잘못 사용하면서 발생하는 많은 문제는 공유된 상태를 바꾸는 알고리즘을 사용하기 때문에 일어난다.

```java
public long sideEffectSum(long n) {
	Accumlator accumlator = new Accumlator();
	LongStream.rangeClosed(1, n).forEach(accumlator::add);
	return accumlator.total;
}

public class Accumlator {
	public long total = 0;
	public void add(long value) { total += value; }
}
```

위 코드 문제점: 본질적으로 순차 실행할 수 있도록 구현되어 있으므로 병렬로 실행하면 참사가 일어난다.

- total을 접근할 때마다(다수의 스레드가 동시에 데이터에 접근하는) 데이터 레이스 문제가 일어난다.
- 여러 스레드에서 동시에 누적자 totla += value을 싱핼하면서 문제가 발생한다.

→ total += value 는 아토믹 연산(atomic operation)이 아니다.

❗️ 병렬 스트림이 올바로 동작하려면 공유된 가변 상태를 피해야 한다.

## 7.1.4 병렬 스트림 효과적으로 사용하기

- 확신이 서지 않으면 직접 측정하라. 순차 스트림과 병렬 스트림 중 어떤 것이 좋을 지 모르겠다면 적절한 벤치마크로 직접 성능을 측정하는 것이 바람직하다.
- 박싱을 주의하라. 되도록이면 기본형 특화 스트림(IntStream, LongStream, DoubleStream)을 사용하는 것이 좋다.
- 순차 스트림보다 병렬 스트림에서 성능이 떨어지는 연산이 있다. 특히 요소의 순서에 의존하는 연산(limit, findFirtst) 을 병렬 스트림에서 수행하려면 비싼 비용을 치러야 한다.
- 스트림에서 수행하는 전체 파이프라인 연산 비용을 고려하라. N(처리해야 할 요소 수), Q(하나의 요소를 처리하는 데 드는 비용) , 전체 스트림 파이프라인 처리 비용: N * Q
- 소량의 데이터에서는 병렬 스트림이 도움 되지 않는다. 소량의 데이터를 처리하는 상황에서는 병렬화 과정에서 생기는 부가 비용을 상쇄할 수 있을 만큼의 이득을 얻지 못한다.
- 스트림을 구성하는 자료구조가 적절한지 확인하라.
- 스트림의 특성과 파이프라인의 중간 연산이 스트림의 특성을 어떻게 바꾸는지에 따라 분해 과정의 성능이 달라질 수 있다.
- 최종 연산의 병합과정 비용을 살펴보라. 병합 과정의 비용이 비싸다면 병렬 스트림으로 얻은 성능의 이익이 서브스트림의 부분 결과를 합치는 과정에서 상쇄될 수 있다.

![2](https://github.com/GDSC-Hongik/2023-2-OC-Java-Study/assets/66353672/bf3dd181-ed36-4d35-b975-0fc755260cde)


## 7.2 포크/ 조인 프레임 워크

포크/ 조인 프레임워크는 병렬화 할 수 있는 작업을 재귀적으로 작은 작업으로 분할한 다음에 서브태스크 각각의 결과를 합쳐서 전체 결과를 만들도록 설계되었다.

## 7.2.1 Recursive Task 활용

스레드 풀을 이용하려면 RecursiveTask<R>의 서브클래스를 만들어야 한다. 

RecursiveTask를 정의하려면 추상 메서드 compute를 구현해야 한다.

```java
protected abstract R compute();

//대부분의 compute 메서드 구현의 의사코드
if(태스크가 충분히 작거나 더 이상 분할할 수 없으면) {
	순차적으로 태스크 계산
} else {
	태스크를 두 서브태스크로 분할
	태스크가 다시 서브태스크로 분할되도록 이 메서드를 재귀적으로 호출함
	모든 서브태스크의 연산이 완료될 때까지 기다림
	각 서브태스크의 결과를 합침
}
```

- 분할 후 정복(divide-and-conquer) 알고리즘의 병렬화 버전이다.

![3](https://github.com/GDSC-Hongik/2023-2-OC-Java-Study/assets/66353672/509cb00f-df48-465e-9d62-09e29de36a6f)


<포크 조인 과정>

```java
// 포크/조인 프레임워크를 이용해서 병렬 합계 수행
import static org.chap07.ParallelStreamsHarness.FORK_JOIN_POOL;

public class ForkJoinSumCalculator extends RecursiveTask<Long> { //RecursiveTask를 상속받아 포크/조인 프레임워크에서 사용할 태스크

    public static final long THRESHOLD = 10_000; // 이 값 이하의 서브태스크는 더 이상 분할 할 수 없다.
    private final long[] numbers;
    private final int start;
    private final int end;

    public ForkJoinSumCalculator(long[] numbers) { // 메인 태스크를 생성할 때 사용할 공개 생성자
        this(numbers, 0, numbers.length);
    }

    private ForkJoinSumCalculator(long[] numbers, int start, int end) { // 메인 태스크의 서브태스크를 재귀적으로 만들 때 사용할 비공개 생성자
        this.numbers = numbers;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        int length = end - start;
        if (length <= THRESHOLD) {
            return computeSequentially(); // 기준값과 같거나 작으면 순차적으로 결과를 계산한다.
        }
        ForkJoinSumCalculator leftTask = new ForkJoinSumCalculator(numbers, start, start + length / 2);
        leftTask.fork();
        ForkJoinSumCalculator rightTask = new ForkJoinSumCalculator(numbers, end + length / 2, end);
        Long rightResult = rightTask.compute(); // 두 번째 서브태스크를 동기 실행한다. 추가로 분할 가능
        Long leftResult = leftTask.join(); // 첫 번째 서브태스크의 결과를 읽거나 아직 결과가 없으면 기다린다.
        return leftResult + rightResult; // 두 서브태스크의 결과를 조합한 값이 이 태스크의 결과다.
    }

    private long computeSequentially() {
        long sum = 0;
        for (int i = start; i < end; i++) {
            sum += numbers[i];
        }
        return sum;
    }

    public static long forkJoinSum(long n) {
        long[] numbers = LongStream.rangeClosed(1, n).toArray();
        ForkJoinTask<Long> task = new ForkJoinSumCalculator(numbers);
				// 보통 ForkJoinPool은 한 번만 인스턴스화해서 정적 필드에 싱글턴으로 저장한다.
        return FORK_JOIN_POOL.invoke(task);
    }
	/*
		LongStream으로 배열을 생성한다. 
		ForkJoinSumCalculator의 생성자로 전달해서, FokrJoinTask를 만든다.
		생성한 태스크를 새로운 ForkJoinPool의 invoke 메서드로 전달한다.
	*/
}
```

## ForkJoinSUmCalculator 실행

- ForkJoinSumCalculator를 ForkJoinPool로 전달하면 풀의 스레드가 ForkJoinSumCalculator의 compute 메서드를 실행하면서 작업을 수행한다.
- compute 메서드는 병렬로 실행할 수 있을 만큼 태스크의 크기가 충분히 작아졌는지 확인하며, 아직 태스크의 크기가 크다고 판단되면 숫자 배열을 반으로 분할해서 두 개의 새로운 ForkJoinSumCalculator로 할당된다.

<img width="573" alt="4" src="https://github.com/GDSC-Hongik/2023-2-OC-Java-Study/assets/66353672/05f7aff2-7f55-43d1-87d5-363b003bc182">

## 7.2.2 포크/조인 프레임워크를 제대로 사용하는 방법

- join 메서드를 태스크에 호출하면 태스크가 생산하는 결과가 준비될 때까지 호출자를 블록시킨다. 따라서 `두 서브태스크가 모두 시작된 다음에 join을 호출해야 한다.`

→ 각각의 스레드태스크가 다른 태스크가 끝나길 기다리는 일이 발생하며 순차 알고리즘보다 느리고 복잡한 프로그램이 되어버릴 수 있다.

- RecursiveTask 내에서는 `ForkJoinPool의 invoke 메서드를 사용하지 말아야 한다.` 대신 compute나 fork 메서드를 직접 호출할 수 있다.

→ 순차 코드에서 병렬 계산을 시작할 때만 invoke를 사용한다.

- 왼쪽 작업과 오른쪽 작업에 모두에 fork 메서드를 호출하는 것이 자연스러울 것 같지만, 한쪽 작업에는 fork를 호출하는 것보다는 compute를 호출하는 것이 효율적이다.

→ 두 서브 태스크의 한 태스크에는 같은 스레드를 재사용할 수 있으므로 풀에서 불필요한 태스크를 할당하는 오버헤드를 피할 수 있다.

- 포크/조인 프레임워크를 이용하는 병렬 계산은 디버깅하기 어렵다.

→ 포크/조인 프레임워크에서는 fork라 불리는 다른 스레드에서 compute를 호출하므로 스택 트레이스가 도움이 되지 않는다.

- 멀티코어에 포크/조인 프레임워크를 사용하는 것이 순차 처리보다 무조건 빠를 거라는 생각은 버려야 한다.

## 7.2.3 작업 훔치기

이론적으로는 코어 개수만큼 병렬화된 태스크로 작업부하를 분할하면 모든 CPU 코어에서 태스크를 실행할 것이고 크기가 같은 각각의 태스크는 같은 시간에 종료될 것이라고 생각한다.

❗️하지만 현실에서는 각각의 서브태스크의 작업완료 시간이 크게 달라질 수 있다.

- 포크/ 조인 프레임워크 에서는 `작업 훔치기(work stealing)` 라는 기법으로 이 문제를 해결한다.

→ 작업 훔치기 기법에서는 ForkJoinPool의 모든 스레드를 거의 공정하게 분할한다. 

- 각각의 스레드는 자신에게 할당된 태스크를 포함하는 이중 연결 리스트(doubly linked list)를 참조하면서 작업이 끝날 때마다 큐의 헤드에서 다른 태스크를 가져와서 작업을 처리한다.
- 이 때 할일이 없어진 스레드는 유휴 상태로 바뀌는 것이라 아니라 다른 스레드 큐의 꼬리에서 작업을 훔쳐온다. 모든 큐가 빌 때까지 이 과정을 반복한다.
    
<img width="735" alt="5" src="https://github.com/GDSC-Hongik/2023-2-OC-Java-Study/assets/66353672/41f5a5dd-f3c6-42ce-9e50-a10d5920f52e">
    

## 7.3 Spliterator 인터페이스

spliterator: 분할할 수 있는 반복자(splitable iterator)

```java
// Spliterator 인터페이스
public interface Spliterator<T> {
	boolean tryAdvance(Consumer<? super T> action);
	Spliterator<T> trySplit();
	long estimateSize();
	int characteristics();
}
```

T: Spliterator에서 탐색하는 요소의 형식

tryAdvance: Spliterator의 일부 요소를 분할해서 두 번째 Spliterator를 생성하는 메서드

estimateSize: 탐색해야 할 요소 수 정보를 제공

## 7.3.1 분할 과정

<img width="729" alt="6" src="https://github.com/GDSC-Hongik/2023-2-OC-Java-Study/assets/66353672/77cf1ada-459c-47e1-9133-c8c5d9818b49">

- trySplit의 결과가 null이 될 때까지 여러 스트림으로 분할하는 과정 반복

## Spliterator 특성

- characteristics: Spliterator 자체의 특성 집합을 포함하는 int를 반환한다.

| 특성 | 의미 |
| --- | --- |
| ORDERD  | 리스트처럼 요소에 정해진 순서가 있으므로 Spliterator는 요소를 탐색하고 분할할 때 이 순서에 유의해야 한다. |
| DISTINCT | x, y 두 요소를 방문했을 때 x.equals(y)는 항상 false를 반환한다. |
| SORTED | 탐색된 요소는 미리 정의된 정렬 순서를 따른다. |
| SIZED | 크기가 알려진 소스(ex. Set)로 Spliterator를 생성했으므로 estimatedSize()는 정확한 값을 반환한다. |
| NON-NULL | 탐색하는 모든 요소는 null이 아니다. |
| IMMUTABLE | 이 Spliterator의 소스는 불변이다. 즉, 요소를 탐색하는 동안 요소를 추가하거나, 삭제하거나, 고칠 수 없다. |
| CONCURRENT | 동기화 없이 Spliterator의 소스를 여러 스레드에서 동시에 고칠 수 있다. |
| SUBSIZED | 이 Spliterator 그리고 분할되는 모든 Spliterator SIZED 특성을 갖는다. |

## 7.3.2 커스텀 Spliterator 구현하기

```java
// 반복형으로 단어 수를 세는 메서드
    public static int countWordsIteratively(String s) {
        int counter = 0;
        boolean lastSpace = true;
        for (char c : s.toCharArray()) { // 문자열의 모든 문자를 하나씩 탐색
            if (Character.isWhitespace(c)) {
                lastSpace = true;
            }
            else {
                if (lastSpace) {
                    counter++; // 문자를 하나씩 탐색하다 공백 문자를 만나면, 지금까지 탐색한 문자를 단어로 간주하여 단어 수 증가
                }
                lastSpace = Character.isWhitespace(c);
            }
        }
        return counter;
    }

System.out.println("Found " + countWordsIteratively(SENTENCE) + " words");
// Found 19 words
```

```java
// 문자열 스트림을 탐색하면서 단어 수를 세는 클래스
private static class WordCounter {
        private final int counter;
        private final boolean lastSpace;

        public WordCounter(int counter, boolean lastSpace) {
            this.counter = counter;
            this.lastSpace = lastSpace;
        }

        public WordCounter accumulate(Character c) { // 문자열의 문자를 하나씩 탐색한다.
            if (Character.isWhitespace(c)) {
                return lastSpace ? this : new WordCounter(counter, true);
            }
            else {
                return lastSpace ? new WordCounter(counter + 1, false) : this; 
								// 문자를 탐색하다 공백을 만나면 지금까지 탐색한 문자 단어로 간주
            }
        }

				// 두 WordCounter의 counter 값을 더함
        public WordCounter combine(WordCounter wordCounter) {
            return new WordCounter(counter + wordCounter.counter, wordCounter.lastSpace);
        }

        public int getCounter() {
            return counter;
        }
    }
```

## WordCounter 병렬로 수행하기

```java
System.out.println("Found" + countWords(stream.parallel()) + " words");
// Found 25 words
```

→ 다른 결과가 나온다.

이유: 원래 문자열을 임의의 위치에서 둘로 나누다보니 예상치 못하게 하나의 단어를 둘로 계산하는 상황이 발생할 수 있다.

→ 문자열을 임의의 위치에서 분할하지 말고 단어가 끝나는 위치에서만 분할하는 방법으로 해결

```java
// WordCounterSpliterator
private static class WordCounterSpliterator implements Spliterator<Character> {

        private final String string;

        private int currentChar = 0;

        public WordCounterSpliterator(String string) {
            this.string = string;
        }

        @Override
        public boolean tryAdvance(Consumer<? super Character> action) {
            action.accept(toString().charAt(currentChar++)); // 현재 문자를 소비한다.
            return currentChar < string.length(); // 소비할 문자가 남아있으면 true를 반환한다,
        }

        @Override
        public Spliterator<Character> trySplit() {
            int currentSize = string.length() - currentChar;
            if (currentSize < 10) {
                return null; 
							// 파싱할 문자열을 순차 처리할 수 있을 만큼 충분히 작아졌음을 알리는 null을 반환한다.
            }
						// 파싱할 문자열의 중간을 분할 위치로 설정한다.
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

##
# [Ch7] 병렬 데이터 처리와 성능

<aside>
✅ **이 장의 내용**

---

- 병렬 스트림으로 데이터를 병렬 처리하기
- 병렬 스트림의 성능 분석
- 포크/조인 프레임워크
- Spliterator로 스트림 데이터 쪼개기
</aside>

- 자바 7의 등장 이전 : 데이터 컬렉션 → 병렬 처리 어려움
    1. 데이터 → 서브파트로 분할
    2. 각 서브파트 스레드에 할당
    3. 적절한 동기화 추가 : race condition 발생 방지
    4. 부분결과 합치기
- 자바 7의 `포크/조인 프레임워크`
- 스트림 이용 : 순차 스트림 → 병렬 스트림 바꾸기 용이

## 7.1 병렬 스트림

- 컬렉션 `parallelStream` 호출
- **병렬 스트림** : 각 스레드에서 처리하기 위해 스트림 요소를 여러 chunk로 분할한 스트림
    - 모든 멀티코어 프로세서에서 각 chunk 처리 가능

```java
// 숫자 n을 인수로 받아 1~n 모든 숫자의 합계 반환하는 메서드
public long sequentialSum (long n) {
	return Stream.iterate(1L, i -> i + 1) 
							 .limit(n)
							 .reduce(0L, Long::sum);
}
```

### 7.1.1 순차 스트림을 병렬 스트림으로 변환하기

```java
// 함수형 리듀싱 연산(숫자 합계 계산) -> 병렬 처리
public long parallelSum (long n) {
	return Stream.iterate(1L, i -> i + 1) 
							 .limit(n)
							 .parallel()  // 스트림을 병렬 스트림으로 변환
							 .reduce(0L, Long::sum);
}
```

- 여러 chunk로 `분할`되어 병렬로 처리됨
- 마지막 부분 결과를 다시 리듀싱 연산으로 `합침`
- parallel과 sequential 중 **최종적**으로 호출된 메서드가 전체 파이프라인에 영향을 미침

<aside>
💡 **Thread Pool 설정**

- 내부적으로 `ForkJoinPool` 사용
- 프로세서 수에 상응하는 스레드를 가짐
</aside>

### 7.1.2 스트림 성능 측정

- 성능 최적화 시 지켜야할 3가지 황금 규칙
    1. `측정`
    2. `측정`
    3. `측정`
- Java Microbenchmark Harness (JMH) 라이브러리로 벤치마크 구현 가능
    - Maven 빌드 도구 사용

|  | sequentialSum | iterativeSum | parallelSum |
| --- | --- | --- | --- |
| 성능(Score) | 121.843 | 3.278 | 604.059 |
- 병렬 버전 → CPU 활용x, 순차 버전에 비해 5배 느림
- 2가지 문제
    - 반복 결과로 박싱된 객체 생성 → 언박싱 필요
    - 반복 작업 쪼개기 어려움 (병렬 수행 어려움)
        - 이전 연산 결과에 따라 다음 함수의 입력이 달라지기 때문

### 더 특화된 메서드 사용

- `LongStream.rangeClosed`
    - 기본형 long 직접 사용 → 박싱/언박싱 오버헤드 ↓
    - 쉽게 chunk로 분할 가능한 숫자 범위 생산

```java
@Benchmark
public long rangedSum() {
	return LongStream.rangeClosed(1, N)
									 .reduce(0L, Long::sum);
}
```

```java
@Benchmark
public long parallelRangedSum() {
	return LongStream.rangeClosed(1, N)
									 .parallel()
									 .reduce(0L, Long::sum)
}
```

|  | rangedSum | parallelRangedSum |
| --- | --- | --- |
| 성능(Score) | 5.315 | 2.677 |
- 올바른 자료구조를 선택해야 병렬 실행도 최적의 성능 발휘할 수 있다
- 멀티코어 간의 데이터 이동은 비쌈
    - 코어 간 데이터 전송 시간보다 훨씬 오래 걸리는 작업만 병렬로 처리하는 것이 바람직

### 7.1.3 병렬 스트림의 올바른 사용법

- 주로 병렬 스트림 잘못 사용하는 경우 : `공유된 상태`를 바꾸는 알고리즘 사용
- data race 문제 : 다수의 스레드에서 동시에 데이터에 접근
    - 올바른 결과값이 나오지 x
    - atomic operation 아닌 경우에 발생
- 공유된 가변 상태를 피해야함

### 7.1.4 병렬 스트림 효과적으로 사용하기

- 확신이 서지 않으면 직접 `측정`하라
    - 벤치마크로 성능 측정
- `박싱`을 주의하라
    - 되도록이면 기본형 특화 스트림 사용
- 순차 스트림보다 병렬 스트림에서 성능이 떨어지는 연산이 있다
    - 요소의 `순서`에 의존하는 연산
    - unordered 호출 → 비정렬된 스트림 → limit 호출
- 스트림에서 수행하는 전체 파이프라인 연산 비용을 고려하라
    - `N*Q`
    - 처리해야 할 요소 개수 `N`
    - 하나의 요소를 처리하는 데 드는 비용 `Q` : 높아지면 병렬 스트림으로 성능 개선 가능성 ↑
- 소량의 데이터에서는 병렬 스트림이 도움되지 않는다
- 스트림을 구성하는 `자료구조`가 적절한지 확인하라
    - **ArrayList**를 LinkedList보다 효율적으로 분할
    - range 팩토리 메서드로 만든 기본형 스트림 → 쉽게 분할 가능
    - 커스텀 Spliterator 구현
- 스트림의 특성 & 파이프라인 중간 연산 → 스트림의 `특성`을 어떻게 바꾸는지에 따라 성능 달라진다
    - SIZED 스트림 → 정확히 같은 크기의 두 스트림으로 분할 → 효과적으로 스트림 병렬 처리
    - filter 연산 → 스트림 길이 예측 x → 효과적x
- 최종 연산의 `병합` 과정 비용을 살펴보라
    - 비싸다면 성능의 이익 상쇄

## 7.2 포크/조인 프레임워크

- 재귀적으로 작은 작업으로 `분할` → subtask크 각각의 결과 `합쳐서` 전체 결과 생성
- subtask를 ForkJoinPool의 스레드에 분산 할당 (Executor 인터페이스)

### 7.2.1 RecursiveTask 활용

- `compute` 메서드 구현
    - task → subtask로 분할
    - 더 이상 분할할 수 없을 때 개별 subtask 결과 생산

```java
// 분할 정복 알고리즘 구조와 유사
if (task가 충분히 작거나 더 이상 분할할 수 없으면) {
	순차적으로 task 계산
} else {
	task를 두 subtask로 분할
	task가 다시 subtask로 분할되도록 이 메서드를 재귀적으로 호출함
	모든 subtask의 연산이 완료될 때까지 기다림
	각 subtask의 결과를 합침
}
```

- 모든 프로세서가 자유롭게 ForkJoinPool에 접근
    - Runtime.availableProcessors의 반환값 → pool에 사용할 스레드 수 결정
    - 가상 프로세서도 개수에 포함됨

### ForkJoinSumCalculator 실행

- 각 subtask 순차적으로 처리
- fork 프로세스 생성
- 이진트리의 task → root에서 역순으로 방문

### 7.2.2 포크/조인 프레임워크를 제대로 사용하는 방법

- 두 subtask가 모두 `시작된 다음에 join` 호출
    - join 호출시 task가 생산하는 결과가 준비될 때까지 호출자 block
    - 각 subtask가 다른 task가 끝나길 기다리는 일 발생
- 순차 코드에서 병렬 계산을 시작할 때만 `invoke` 사용
- 한쪽 작업에는 fork, 다른쪽 작업에는 compute 호출
    - 한 task에서는 같은 스레드 재사용 가능 → task 할당 오버헤드↓
- 포크/조인 프레임워크의 병렬 계산은 디버깅 어려움
    - fork라 불리는 다른 스레드에서 compute 호출
    - stack trace 도움 x
- 멀티코어에 포크/조인 프레임워크 사용 → 순차처리보다 항상 빠르지 x
    - task를 여러 독립적인 subtask로 분할 가능해야함
    - 각 subtask의 실행시간 > 새로운 task fork하는데 드는 시간

### 7.2.3 작업 훔치기

- 코어 개수와 관계없이 적절한 크기로 분할된 많은 task를 fork하는 것이 바람직
- `work stealing` : ForkJoinPool의 모든 스레드 거의 공정하게 분할
    - 할당된 task를 더 빨리 처리하여 할일이 없어진 스레드 → 다른 스레드의 큐의 tail에서 작업 훔쳐옴
    - 모든 큐가 빌 때까지 과정 반복
- 풀에 있는 작업자 스레드의 task 재분배 & 균형 맞춤

## 7.3 Spliterator 인터페이스

- 자바8에 등장
- splitable iterator : 분할할 수 있는 반복자
- 자동으로 스트림을 분할하는 기법

```java
// T : 탐색하는 요소의 형식
public interface Spliterator<T> {
	boolean tryAdvance(Comsumer<? super T> action);
	Spliterator<T> trySplit();
	long estimateSize();
	int characteristics();
}
```

- `tryAdvance` : 탐색해야할 요소가 남아있으면 true 반환
- `trySplit` : Spliterator의 일부 요소 분할 → 두번째 Spliterator 생성
- `estimateSize` : 탐색해야할 요소 수 정보 제공

### 7.3.1 분할 과정

- trySplit이 `null`이 될 때까지 Spliterator에 trySplit 호출 → 새로운 Spliterator 생성
    - null 반환 → 더 이상 자료구조 분할 x
    - 재귀 분할 과정 종료

### Spliterator 특성

- `characteristics` : Spliterator 특성
- 참고하여 Spliterator 더 잘 제어하고 최적화

| 특성 | 의미 |
| --- | --- |
| ORDERED | 요소에 정해진 순서가 있음 |
| DISTINCT | x, y 두 요소를 방문했을 때 x.equals(y) → 항상 false 반환 (요소 항상 서로 다름) |
| SORTED | 탐색된 요소 → 미리 정의된 정렬 순서 따름 |
| SIZED | 크기가 알려진 소스로 Spliterator 생성 → estimatedSize 정확한 값 반환 |
| NON-NULL | 탐색하는 모든 요소가 null이 아님 |
| IMMUTABLE | 소스 불변 (탐색하는 동안 요소 추가/삭제/수정 불가) |
| CONCURRENT | 동기화 없이 Spliterator의 소스 여러 스레드에서 수정 가능 |
| SUBSIZED | 이 Spliterator & 분할되는 모든 Spliterator → SIZED 특성을 가짐 |

### 7.3.2 커스텀 Spliterator 구현하기

```java
// 반복형으로 단어 수를 세는 메서드
public int countWordsIteratively(String s) {
	int counter = 0;
	boolean lastSpace = true;
	for (char c : s.toCharArray()) {
		if (Character.isWhitespace(c)) {
			lastSpace = true;
		} else {
			if (lastSpace) counter++;
			lastSpace = false;
		}
	}
	return counter;
}
```

### 함수형으로 단어 수를 세는 메서드 재구현하기

- String → 스트림으로 변환 `Stream<Character>`

```java
// 문자열 스트림을 탐색하며너 단어 수를 세는 클래스
class WordCounter {
	private final int counter;
	private final boolean lastSpace;
	public WordCounter(int counter, boolean lastSpace) {
		this.counter = counter;
		this.lastSpace = lastSpace;
	}
	public WordCounter accumulate(Character c) {
		if (Character.isWhiteSpace(c)) {
			return lastSpace ? this : new WordCounter(counter, true);
		} else {
			return lastSpace ? new WordCounter(counter+1, false) : this;
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

```java
private int countWords(Stream<Character> stream) {
		WordCounter wordCounter = stream.reduce(new WordCounter(0, true),
																								WordCounter::accumulate,
																								WordCounter::combine);
		return wordCounter.getCounter();
}
```

### WordCounter 병렬로 수행하기

- 스트림 분할 위치에 따라 잘못된 결과 나올수 있음
    - 예상치 못하게 하나의 단어를 둘로 계산하는 상황
- 단어가 끝나는 위치에서만 분할하는 문자 Spliterator 필요

```java
// WordCounterSpliterator
class WordCounterSpliterator implements Spliterator<Character> {
	private final String string;
	private int currentChar = 0;
	public WordCounterSpliterator(String string) {
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
		for (int splitPos = currentSize / 2 + currentChar;
					splitPos < string.length(); splitPos++) {
			if (Character.isWhitespace(string.charAt(splitPos))) {
				Spliterator<Character> spliterator =
					new WordCounterSpliterator(string.substring(currentChar, splitPos));
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
		return ORDERED + SIZED + SUBSIZED + NON-NULL + IMMUTABLE;
	}
}
```

- ORDERED : 문자열의 문자 등장 순서 유의미
- SIZED : estimatedSize 반환값 정확
- SUBSIZED : trySplit로 생성된 Spliterator도 정확한 크기
- NONNULL : 문자열에 null문자 존재하지x
- IMMUTABLE : 문자열 자체가 불변 클래스, 문자열 파싱하면서 속성 추가x

### WordCounterSpliterator 활용

```java
Spliterator<Character> spliterator = new WordCounterSpliterator(SENTENCE);
Stream<Character> stream = StreamSupport.stream(spliterator, true);
```

- true → 병렬 스트림 생성 여부
- `늦은 바인딩 Spliterator`의 요소 바인딩 시점
    - 첫번째 탐색 시점
    - 첫번째 분할 시점
    - 첫번째 예상 크기(estimatedSize) 요청 시점
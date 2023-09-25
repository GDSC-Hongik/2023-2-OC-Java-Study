# 이펙티브 자바 - Item 48: 스트림 병렬화는 주의해서 적용하라

자바로 동시성 프로그램을 작성하기가 점점 쉬워지고 있으나, 이를 올바르고 빠르게 작성하는 일은 여전히 어려운 작업이다. 동시성 프로그래밍을 할 때는 안전성(safety)과 응답 가능(liveness) 상태를 유지하기 위해 애써야 하는데, 병렬 스트림 파이프라인 프로그래밍에서도 다를 바 없다.

```java
// Stream-based program to generate the first 20 Mersenne primes
// 메르센 소수: 2의 거듭제곱에서 1이 모자란 숫자
public static void main(String[] args) {
	primes().map(p -> TWO.pow(p.intValueExtract()).subtract(ONE))
		.filter(mersenne -> merseene.isProbablePrime(50))
		.limit(20)
		forEach(System.out::println);
}

static Stream<BigInteger> primes() {
	return Stream.iterate(TWO, BigInteger::nextProbablePrime);
}
```

- 위 코드에 실행 속도를 높이기 위해 스트림 파이프라인의 parallel()을 호출하면 어떻게 될까?

→ 아무것도 출력하지 못하면서 CPU는 90%나 잡아먹는 상태가 무한히 계속된다.

- 이유
    - 스트림 라이브러리가 이 파이프라인을 병렬화하는 방법을 찾아내지 못했기 때문이다. 환경이 아무리 좋더라도 데이터 소스가 Stream.iterate거나 중간 연산으로 limit를 쓰면 파이프라인 병렬화로는 성능 개선을 기대할 수 없다.
    - 파이프라인 병렬화는 limit를 다룰 때 CPU 코어가 남는다면 원소를 몇 개 더 처리한 후 제한된 개수 이후의 결과를 버려도 아무런 해가 없다고 가정한다.
        
        → 위 코드에서는 원소 하나를 계산하는 비용이 대략 그 이전까지의 원소 전부를 계산한 비용을 합친만큼 든다.
        

<aside>
💡 스트림 파이프라인을 마구잡이로 병렬화하면 안 된다. 성능이 오히려 나빠질 수 있다.

</aside>

📌 대체로 스트림의 소스가 ArrayList, HashMap, HashSet, ConcurrentHashMap의 인스턴스거나 배열, int 범위, long 범위일 때 병렬화의 효과가 가장 좋다.

- 이 자료구조들은 모두 데이터를 `원하는 크기로 정확하고 손쉽게 나눌 수 있어서` 일을 다수의 스레드에 분배하기 좋다는 특징이 있다.
- 원소들을 순차적으로 실행할 때의 참조 지역성이 뛰어나다는 것이다. 이웃한 원소의 참조들이 메모리에 연속해서 저장되어 있다는 뜻이다.

❗️ 참조들이 가리키는 실제 객체가 메모리에서 떨어져 있으면 참조 지역성이 나빠진다. 참조 지역성이 낮으면 스레드는 데이터가 주 메모리에서 캐시 메모리로 전송되어 오기를 기다리며 대부분 시간을 멍하니 보내게 된다. 

→ `참조 지역성`은 병렬화할 때 아주 중요한 요소로 작용한다. 참조 지역성이 가장 뛰어난 자료구조는 기본 타입의 배열이다.

📌 스트림 파이프라인의 종단 연산의 동작 방식도 병렬 수행 효율에 영향을 준다. 

- 종단 연산에서 수행하는 작업량이 파이프라인 전체 작업에서 상당 비중을 차지하면서 순차적인 연산이라면 파이프라인 병렬 수행의 효과는 제한된다.
- reduce, min, max, count, sum 과 같은 축소 연산이 병렬화에 적합하다.
- anyMatch, allMatch, noneMatch처럼 조건에 맞으면 바로 반환되는 메서드도 적합하다.
- 가변 축소를 수행하는 collect 메서드는 병렬화에 적합하지 않다.

<aside>
💡 스트림을 잘못 병렬화하면 성능이 나빠질 뿐만 아니라 결과 자체가 잘못되거나 예상 못한 동작이 발생할 수 있다.

</aside>

📌 안전 실패(safety failure): 병렬화한 파이프라인이 사용하는 mappers, filters, 혹은 다른 함수 객체가 명세대로 동작하지 않을 때 벌어질 수 있다.

- Stream 명세는 사용되는 함수 객체에 관한 엄중한 규약을 정의해놨다.
    - reduce 연산의 accumulator와 combiner 함수는 결합 법칙을 만족하고, 간섭받지 않고, 상태를 갖지 않아야 한다. 이를 지키지 못해도 순차적 수행에서는 올바른 결과를 얻을 수 있지만 병렬로 수행한다면 실패한다.

❗️ 변경 전후로 반드시 성능 테스트를 하여 병렬화를 사용할 가치가 있는지 확인해야 한다.

<aside>
💡 조건이 잘 갖춰지면 parallel 메서드 호출 하나로 거의 프로세서 코어 수에 비례하는 성능 향상을 만끽할 수 있다.

</aside>

```java
// n 보다 작거나 같은 소수 갯수 계산
// Prime-counting stream pipeline - benefits from parallelization
static long pi(long n) {
	return LongStream.rangeClosed(2, n)
		.mapToObj(BigInteger::valueOf)
		.filter(i -> i.isProbablePrime(50))
		.count();

// Prime-couting stream pipeline - parallelVersion
static long pi(long n) {
	return LongStream.rangeClosed(2, n)
		.parallel()
		.mapToObj(BigInteger::valueOf)
		.filter(i -> i.isProbablePrime(50))
		.count();
```

- parallel()을 적용하면 속도가 훨씬 빨라진다.

📌 무작위 수들로 이루어진 스트림을 병렬화하려거든 TheadLocalRandom 보다는 SplittableRandom 인스턴스를 이용하자. SplittableRandom은 정확히 이럴때 쓰고자 설계된 것이라 병렬화하면 성능이 선형으로 증가한다.

# 결론

스트림의 많은 코드에서 스트림 병렬화가 효과를 보는 경우가 많지 않다. 스트림 병렬화를 하지 말라는 뜻은 아니나, 조건이 잘 갖춰졌을때 사용해야만, parallel 메서드 호출 하나로 거의 프로세서 코어 수에 비례하는 성능 향상을 수행할 수 있다.
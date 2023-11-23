# ****ITEM 48: 스트림 병렬화는 주의해서 적용하라****

## 자바의 동시성 프로그래밍

- 자바의 동시성 프로그래밍 : wait/notify → Executor → fork-join → parallel 스트림
- 그러나 여전히 어려움 : `safety`, `liveness`

<aside>
💡 데이터 소스가 `Stream.iterate`거나 중간 연산으로 `limit` 사용 → 병렬화로 성능 개선 x

</aside>

- 파이프라인 병렬화에서 `limit`을 사용한다는 것은..
    - CPU 코어가 남는다면 원소를 몇 개 더 처리
    - 제한된 개수 이후의 `결과를 버려도 상관없다`고 가정

## 병렬화하기 좋은 경우

### 참조 지역성 good

- `ArrayList` `HashMap` `HashSet` `ConcurrentHashMap`
- `배열` `int 범위` `long 범위`
- 데이터를 원하는 크기로 정확하고 손쉽게 나눌수 있음
    - 다수에 스레드에 분배하기 좋음
- 원소들을 순차적으로 실행할때의 참조 지역성 ↑
    - 메모리에 연속해서 저장됨

### 종단 연산

- 작업량 비중↑, 순차적인 연산 → 파이프라인 병렬 수행 효과 ↓
- `축소`
    - reduce, min, max, count, sum
    - 가변 축소 collect → 적합하지x
- `조건`에 맞으면 바로 반환되는 메서드
    - anyMatch, allMatch, noneMatch

### spliterator 메서드 재정의

- spliterator 메서드 재정의 & 병렬화 성능 테스트 필요

## 마무리

### 안전 실패

- safety failure
- 스트림을 잘못 병렬화하면 결과가 잘못되거나 오동작 발생 가능

### 함수 객체에 관한 엄중한 규약

- `reduce`연산의 `accumulator`, `combiner` 함수
    - 반드시 결합법칙 만족
    - 간섭받지 않아야함
    - 상태 갖지 않아야함
- 조건이 잘 갖춰지면, parallel 메서드 호출로 프로세서 코어 수에 비례하는 성능↑

<aside>
💡 반드시 성능 `테스트`하여 병렬화를 사용할 가치가 있는지 확인하자 !!

</aside>
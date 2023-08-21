# Java 기초 - HashMap은 어떻게 동작하는가?

## Java HashMap은 어떻게 동작하는가?

- Map 인터페이스 자체는 Java 5에서 Generic이 적용된 것 외에 처음 선보인 이후 변화가 없다
- HashMap 구현체는 성능을 향상시키기 위해 지속적으로 변화해 왔다

## HashMap 과 HashTable

정의: 키에 대한 `해시 값`을 사용하여 값을 저장하고 조회하며, `키-값 쌍`의 개수에 따라 `동적으로 크기가 증가`하는 associate array

### HashMap

- Java2에서 보인 Java Collection Framework에 속하는 API
- Map 인터페이스를 구현하고 있다.
- 보조 해시 함수(Additional Hash Function)를 사용한다. 따라서 HashTable에 비해 해시 충돌(hash collision)이 덜 발생한다.

### HashTable

- JDK 1.0 부터 있던 Java의 API
- Map 인터페이스를 구현하고 있다.
- 구현에는 거의 변화가 없다. JRE 1.0, JRE 1.1 환경을 대상으로 구현한 Java 애플리케이션이 잘 동작할 수 있도록 하위 호환성을 제공

📌 associate array

- HashTable에서는 Dictionary라는 용어을 사용
- HashMap에서는 Map이라는 용어를 사용
    - 키 집합인 정의역과 값 집합인 공역의 대응에 해시 함수를 이용한다

## 해시 분포와 해시 충돌

📌 완전한 해시 함수(perfect hash functions)

- X.equals(Y)가 '거짓'일 때 X.hashCode() != Y.hashCode()가 같지 않다. 이때 사용하는 해시 함수이다.
- `Boolean`(구별되는 객체의 종류가 적은), Integer, Long, Double(`Number 객체)`는 객체가 나타내려는 값 자체를 해시 값으로 사용할 수 있다. 따라서 완전한 해시 함수 대상으로 삼을 수 있다.
- `String`이나 `POJO`(plain old java object)에 대해 완전한 해시 함수를 제작하는 것은 불가능하다.

HashMap은 기본적으로 각 객체의 hashCode() 메서드가 반환하는 값을 사용

- 결과 자료형은 int다. 하지만 32 비트 정수 자료형으로는 완전한 자료 해시를 만들 수 없음
    
    → 논리적으로 생성 가능한 객체의 수가 2^32 보다 많을 수 있기 때문이다.
    
    → O(1)을 보장하기 위해, 랜덤 접근 가능하게 하려면 원소가 2^32인 배열을 모든 HashMap이 갖고있어야 하기 때문이다.
    

따라서 HashMap을 비롯한 해시 함수를 이용하는 associative array 구현체에서는 메모리 절약을 위해 |N| 보다 작은 M개의 원소를 이용한다

```jsx

//해시를 사용하는 associaitive array 구현체에서 저장/조회할 해시 버킷 계산 방법
int index = X.hashCode() % M;
```

### 문제점

서로 다른 해시 코드를 가진 서로 다른 객체가 1/M의 확률로 같은 해시 버킷을 사용한다.

이러한 해시 충돌이 발생하더라도 키-값 쌍 데이터를 저장하고 조회할 수 있게 하는 방법

1. Open Addressing
2. Seperate Chaining

![Untitled](https://github.com/xogns1514/test/assets/66353672/38db93c7-050a-439a-9340-6d97551e942d)


### Open Addressing

- 데이터 삽입시, 해시 버킷이 이미 사용중인 경우 다른 해시 버킷에 해당 데이터를 삽입
- 데이터 저장/조회할 버킷을 찾을 때, Linear Probing, Quadratic Probing을 사용

### Seperate Chaining

- 각 배열의 인자는 인덱스가 같은 해시 버킷을 연결한 링크드 리스트의 첫 부분

→ 둘다 Worst Case O(M) 이다

→ Open Addressing은 연속된 공간에 데이터를 저장해 Seperate Chaining에 비해 캐시 효율이 높다.

→ 하지만 M값이 커지면 L1, L2 캐시 적중률이 줄어들기 때문에(데이터 분산으로 인해), 장점이 사라진다.

Java HashMap에서는 Serperate Chaining 방식을 사용한다.

- Open Addresing은 데이터 삭제시 효율적이기 어렵다
  → 삭제가 빈번하게 발생을 하면, 실제 데이터가 없더라도 검색을 할때, Dummy Node에 의해서, 많은 Bucket을 연속적으로 검색함
- HashMap에서 remove() 메서드는 자주 호출될 수 있다
- 키-값 개수가 일정 개수를 넘어가면, 일반적으로 Open Addressing이 더 느리다
- Seperate Chaining 방식에서 해시 충돌이 잘 발생하지 않도록 조정만 하면 Worst Case의 일은 줄일 수 있다

### Java 7에서의 put() 메서드 구현

```jsx
public V put(K key, V value) { if (table == EMPTY_TABLE) { inflateTable(threshold); 
        int hash = hash(key);

        // i 값이 해시 버킷의 인덱스
        // indexFor() 메서드는 hash % table.length와 같은 의도의 메서
        int i = indexFor(hash, table.length);

        // 해시 버킷에 있는 링크드 리스트를 순회
        // 만약 같은 키가 이미 저장되어 있다면 교체
        for (Entry<K,V> e = table[i]; e != null; e = e.next) {
            Object k;
            if (e.hash == hash && ((k = e.key) == key || key.equals(k))) {
                V oldValue = e.value;
                e.value = value;
                e.recordAccess(this);
                return oldValue;
            }
        }

        // 삽입, 삭제 등으로 이 HashMap 객체가 몇 번이나 변경(modification)되었는지
        // 관리하기 위한 코드
        // ConcurrentModificationException를 발생시켜야 하는지 판단할 때 사용
        modCount++;

        // 아직 해당 키-값 쌍 데이터가 삽입된 적이 없다면 새로 Entry를 생성
        addEntry(hash, key, value, i);
        return null;
    }
```

### Java 8 HashMap에서의 Seperate Chaining

객체의 해시 함수 값이 균등 분포상태고 할때 

- Java 2~Java 7에서 get() 메서드 호출에 대한 기댓값: E(N/M)
- Java 8에서 get() 메서드 호출에 대한 기댓값: E(Log(N/M))

 Java 8에서는 데이터의 개수가 많아 지면, `링크드 리스트 대신 트리`를 사용하기 때문이다.
 → 레드-블랙 트리를 이용한다.

- Java 8 HashMap에서는 하나의 해시 버킷에 8개의 키-값 쌍이 모이면 링크드 리스트를 트리로 변경한다.
- 해당 버킷 데이터를 삭제하여 개수가 6개에 이르면 다시 링크드 리스트로 변경한다.

→ 트리는 링크드 리스트보다 메모리 사용량이 많음, 데이터 수 적을 때 차이 없음

Java 8 HashMap에서는 `Entry 클래스 대신 Node 클래스`를 사용

- Java 7의 Entry 클래스와 내용이 같음
- 링크드 리스트 대신 트리를 사용할 수 있도록 하위 클래스인 TreeNode가 있다는 것이 다름

## 해시 버킷 동적 확장

HashMap은 키-값 쌍 데이터 개수가 일정 개수 이상이 되면, `해시 버킷의 개수를 두 배`로 늘림

→ 해시 버킷 개수가 적다면 메모리 아낄 수 있음, 하지만 해시 충돌로 성능 손실이 있기 때문이다.

- 해시 버킷 개수 기본값: 16
- 버킷의 최대 개수: 2^30
- 버킷 개수 두 배로 증가할때마다, 모든 키-값 데이터를 읽어 새로운 Seperate Chaining을 구성해야함

→ 데이터 개수 예측 가능한 경우, 생성자 인자로 초기 해시 버킷 개수를 지정해 불필요한 Seperate Chaining을 재구성 하지 않게 할 수 있다.

### 해시 버킷 두 배 확장의 문제점

- 해시 버킷의 개수 M이 2^a 형태가 되기 때문에,
    
     index = X.hashCode() % M을 계산할 때 X.hashCode()의 하위 a개의 비트만 사용
    

→ 해시 값을 2의 승수로 나누면 해시 충돌이 쉽게 발생

## 보조 해시 함수

index = X.hashCode() % M을 계산할 때, M 값은 소수일 때 index 값 분포가 가장 균등하다

하지만 위 처럼 M값이 소수가 아니기 때문에, `보조 해시 함수`를 통해  `index 값 분포가 가급적 균등`하게 한다.

- 보조 해시 함수의 목적: ‘키’의 해시 값을 변형해, 해시 충돌 가능성을 줄인다.

Java 7 HashMap에서의 보조 해시 함수

```jsx
final int hash(Object k) {  
        /* Java 7부터는 JRE를 실행할 때, 데이터 개수가 일정 이상이면
         String 객체에 대해서 JVM에서 제공하는 별도의 옵션으로
         해시 함수를 사용하도록 할 수 있다.
         만약 이 옵션을 사용하지 않으면 hashSeed의 값은 0이다.
				*/
        int h = hashSeed;
        if (0 != h && k instanceof String) {
            return sun.misc.Hashing.stringHash32((String) k);
        }
        h ^= k.hashCode();
        /* 해시 버킷의 개수가 2a이기 때문에 해시 값의 a비트 값만을 
	         해시 버킷의 인덱스로 사용한다. 따라서 상위 비트의 값이 
	         해시 버킷의 인덱스 값을 결정할 때 반영될 수 있도록
	         shift 연산과 XOR 연산을 사용하여, 원래의 해시 값이 a비트 내에서 
	         최대한 값이 겹치지 않고 구별되게 한다.
				*/
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }
```

Java 8 HashMap에서의 보조 해시 함수

```jsx
static final int hash(Object key) { 
					int h; 
					return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16); }
```

- 링크드 리스트 대신 트리를 사용하므로 해시 충돌 시 발생할 수 있는 성능 문제가 완화되었기 때문
- 최근의 해시 함수는 균등 분포가 잘 되게 만들어지는 경향이 많음

## String 객체에 대한 해시 함수

String 객체에 대한 해시 함수 수행 시간은 문자열 길이에 비례

### JDK 1.1 에서 String 객체에 대한 해시 함수 수행

```jsx
public int hashCode() {  
    int hash = 0;
     int skip = Math.max(1, length() / 8);
     for (int i = 0; i < length(): i+= skip) 
           hash = s[i] + (37 * hash);
    return hash;
}
```

- 문자열의 길이가 16을 넘으면 최소 하나의 문자를 건너가며 해시 함수를 계산

### 문제점

- 웹상의 URL은 길이가 수십 글자에 이르면서 앞 부분은 동일하게 구성되는 경우가 많음

→ 서로 다른 URL의 해시 값이 같아지는 빈도가 매우 높아짐

### Java 8 에서 String 객체에 대한 해시 함수 수행

```jsx
public int hashCode() {  
        int h = hash;
        if (h == 0 && value.length > 0) {
            char val[] = value;

            for (int i = 0; i < value.length; i++) {
                h = 31 * h + val[i];
		/*
		31을 사용하는 이유는, 31은 소수 이며, 
		어떤 수에 31을 곱하는 것은 빠르게 계산 가능하기 때문이다(31N = 32N - N)
		32 = 2^5(shift연산으로 가능) 
		*/
            }
            hash = h;
        }
        return h;
    }
```

### Java 7에서 String 객체에 대한 별도의 해시 함수

HashMap에 저장된 키-값 쌍이 일정 개수 이상이면 String 객체에 한하여 별도의 해시 함수를 사용할 수 있게 하는 기능(Java 8에는 없는 삭제된 기능이다.)

- HashMap에 저장된 키-값 쌍이 일정 개수 이상일 때 String 객체에 String 클래스의 hashCode() 메서드 대신 sun.misc.Hashing.stringHash32() 메서드를 사용
- hash32() 메서드는 MurMur 해시를 구현한 것
- MurMur 해시를 이용하여 String 객체에 대한 해시 충돌을 매우 낮출 수 있었다

### 문제점

- MurMur 해시는 hash seed를 필요 (sun.misc.Hashing.randomHashSeed())
- 메서드에서는 Random.nextInt() 메서드를 사용

→ Random.nextInt() 메서드는 compare and swap(CAS) 연산을 사용하는 AtomicLong을 사용하는데, CAS 연산은 코어가 많을수록 성능이 떨어짊
# [1] ****Java의 정의와 동작 방식****

<aside>
✅ **다루는 내용**

- JVM과 메모리 구조
- 클래스 로딩 등 Java의 동작 방식
- Java 바이트코드와 JIT(AOT) 컴파일러
- Java가 지원하는 GC 알골리즘
- 스레드 동기화
</aside>

## Java란?

- WORE 사상 기반
    - Write Once, Run Everywhere
- JVM을 통해 작동
- OOP이지만 순수한 OOP는 아닌
    - primitive type : 일반 변수는 공유 가능
    - static method : 인스턴스 생성 없이 호출
    - wrapper class에서 auto boxing/unboxing을 통해 primitive type 변수 사용

<aside>
💡 **wrapper class** : primitive type을 객체로 다루기 위해서 사용하는 클래스

- 값을 포장하여 객채로 만듦 (boxing)
- 값을 더하거나 변환시키는 경우 포장을 다시 뜯음 (unboxing)
- JDK 1.5부터 boxing과 unboxing 필요한 경우에 컴파일러가 자동으로 처리 (auto)
</aside>

## Java 아키텍처

![Untitled](https://file.notion.so/f/s/89be4b96-5b3a-4186-b7b9-3e49230d9e7d/Untitled.png?id=e375f7a0-2f55-4820-b8f5-1422c7e76d7d&table=block&spaceId=98c015d1-def8-4399-931a-47453151ae6d&expirationTimestamp=1692158400000&signature=lxFtPI5B6WORM3UjvUUFrobo6COicDZR0rmkL8JXXaM&downloadName=Untitled.png)

- **Java 플랫폼** : JDK(SE, EE, ME) 구현한 제품, Java 개발 및 실행 환경
- **JDK** : JRE + Dev tools (자바 개발 킷)
- **JRE** : JVM + Library (자바 실행 환경)
- **JVM** : Java 바이트코드를 기계어로 변환 및 실행 (자바 가상 머신)

## JDK 구성 요소

![Untitled](https://file.notion.so/f/s/2a948ad5-8a2b-4186-aa2c-1f5678aa7f25/Untitled.png?id=cc19d987-563e-4ada-82c8-7baeab4311d5&table=block&spaceId=98c015d1-def8-4399-931a-47453151ae6d&expirationTimestamp=1692158400000&signature=n4mxogBUu7PLuV_X_UaKKu8-8oUMPsovQJsMrGe5v2E&downloadName=Untitled.png)

### JDK & JRE

![Untitled](https://file.notion.so/f/s/c4072fd2-24c1-46a1-a8c7-b87f974b6723/Untitled.png?id=9095d01c-6397-49ad-87f1-5f4208ef693e&table=block&spaceId=98c015d1-def8-4399-931a-47453151ae6d&expirationTimestamp=1692158400000&signature=3coqDgvgP3yo_DHN1plCoF00KEGTe3vBGJk1jCLhV_8&downloadName=Untitled.png)

- Java 프로그램 실행 및 개발 환경 제공
- JDK 11 이후 JRE 포함
    - 기존 JRE : JVM, Class Loader, Java Class Libraries, Resources 등 포함
- JPMS로 내부 기능 모듈화 (JDK 9에 도입)
    - Java Platform Module System
    - 필요한 모듈 → 커스텀 JRE 생성 : 메모리, 용량 절약

<aside>
💡 **모듈** vs **컴포넌트**

- 모듈 : 코드 & 데이터 그룹화, 재사용 가능, 정적 단위
- 컴포넌드 : 독립적으로 실행, 소프트웨어 단위
</aside>

### JVM

![Untitled](https://file.notion.so/f/s/4e5de664-889e-42cf-b34e-00b33fd8d26d/Untitled.png?id=ef5f98e7-4abe-48fe-baf5-50e34e81dfe9&table=block&spaceId=98c015d1-def8-4399-931a-47453151ae6d&expirationTimestamp=1692158400000&signature=jl947hCSnY-JSGWfoQudPnjyY86tPGs6HA71tqcjhVo&downloadName=Untitled.png)

- Java Virtual Machine
- Java 앱 실행 주체, 다양한 플랫폼에서 동작 가능
- 대표적인 기능
    - **클래스 로딩**
    - **메모리 관리 (GC)**
    - **스레드 관리**
    - **예외 처리**

### JVM Architecture

![Untitled](https://file.notion.so/f/s/0e18cbc0-a4ae-4028-bd44-3392f1f29134/Untitled.png?id=95a18716-a7ab-4bbc-94e2-d3e58f0ad68a&table=block&spaceId=98c015d1-def8-4399-931a-47453151ae6d&expirationTimestamp=1692158400000&signature=T-xWQ7dvn_FEWm_ljcZ9qHbLPc8DMFnnDCUl3k_jav8&downloadName=Untitled.png)

- **Class Loader** : 바이트 코드 로딩, 검증, 링킹
- **Runtime Data Access** : 앱 실행을 위해 사용
- **Execution Engine** : 메모리 영역의 data를 가져와 작업 수행
- **JNI** : JVM과 Native Library(C/C++) 간 이진 호환을 위한 인터페이스
    - Java Native Interface

<aside>
💡 **native code**가 사용되는 경우

- 하드웨어 자체 기능을 동작시킬 때
- 프로세스 성능 향상시킬 때
- 이미 native code로 작성된 라이브러리를 사용하고 싶을 때
</aside>

- **Native Library** : native method의 구현체 포함

## Java 동작 방식

![Untitled](https://file.notion.so/f/s/3688eb3e-09cc-42ce-8089-a1faeed9b7e6/Untitled.png?id=d57442ed-f487-4787-a161-62f0300233c5&table=block&spaceId=98c015d1-def8-4399-931a-47453151ae6d&expirationTimestamp=1692158400000&signature=j5Coybi_qXY0Ll0hXx5pSOwIzR31IN0zrlI5tMmFOpo&downloadName=Untitled.png)

- JVM 아키텍처

![Untitled](https://file.notion.so/f/s/2232a35e-19fa-4173-b685-221d65148c47/Untitled.png?id=1e6068db-1aa4-4e59-9d79-8cb6483e1f1f&table=block&spaceId=98c015d1-def8-4399-931a-47453151ae6d&expirationTimestamp=1692158400000&signature=-bvDDd460GOBBn_jRPdENPgjkBr_XrAOG3f22DzXT8c&downloadName=Untitled.png)

- JIT 컴파일러 내부

![Untitled](https://file.notion.so/f/s/d8c17dc8-2f2e-4495-a282-f557471c4007/Untitled.png?id=d843cf4a-db06-41c1-b94a-eb5f6db2c1b9&table=block&spaceId=98c015d1-def8-4399-931a-47453151ae6d&expirationTimestamp=1692158400000&signature=rP8Yi_Aps7YygYRyonBYyAMJSONNREUQS0Y9xua6EpQ&downloadName=Untitled.png)

### Class Loaders

![Untitled](https://file.notion.so/f/s/70f16b2b-3a88-4ee1-a570-d90483ea12b5/Untitled.png?id=151ecbd5-56c7-4a9c-bb7f-535e7c032ede&table=block&spaceId=98c015d1-def8-4399-931a-47453151ae6d&expirationTimestamp=1692158400000&signature=O8ac8is00mZ3PsY00_46bosJ2Y369QaXYYFjRNYKNM8&downloadName=Untitled.png)

- **런타임**에 Java 클래스/인터페이스의 바이트코드 → 동적으로 메모리에 loading
    - 필요할 때마다 load
- **Loading** : JVM이 필요한 클래스 파일 load (.class)
- **Linking** : load된 클래스의 verify, prepare, resolve 작업 수행
    - verify : 읽어들인 클래스가 자바 언어 or JVM 명세에 맞게 잘 구성되었는지 확인
    - prepare : 클래스가 필요로 하는 메모리 할당 및 데이터 구조 준비 (필드, 메소드, 인터페이스)
    - resolve : 심볼릭 메모리 레퍼런스 → 메소드 영역의 실제 레퍼런스로 교체
- **Initialization** : 클래스/정적 변수 초기화 (static)

### JVM Run-Time Data Areas

![Untitled](https://file.notion.so/f/s/f0ad7b80-2b8f-418a-bb70-4ec8f5e54047/Untitled.png?id=fcd4ebef-e581-432c-91a4-4cc9c4e99767&table=block&spaceId=98c015d1-def8-4399-931a-47453151ae6d&expirationTimestamp=1692158400000&signature=Iio_Sf4OsiPD9StVrCqvh8gjL1mWJq_6DKgt4dnS9CE&downloadName=Untitled.png)

- **PC Register** : thread별로 생성, 실행중인 명령(offset) 저장
- **JVM Stack** : thread별로 생성, method 실행 관련 정보 저장 (frame)
- **Heap** : JVM 실행시 생성, 모든 객체 인스턴스 / 배열 관련 메모리 할당
- **Method Area** : JVM 실행시 생성, 클래스 구조나 정보 저장
- **Native Method Stack** : thread별로 생성, native code 실행 관련 정보 저장

### Execution Engine

![Untitled](https://file.notion.so/f/s/74bd7ae6-2f0e-4964-9a29-9626e763798a/Untitled.png?id=1970755a-fb20-4d4f-9a13-d9a5c3af7faf&table=block&spaceId=98c015d1-def8-4399-931a-47453151ae6d&expirationTimestamp=1692158400000&signature=BXmP_hJi7XaU0tMhwzoaTOPMIY_3NrfMOmy-JC8zsO4&downloadName=Untitled.png)

- JVM 메모리 영역에 있는 바이트코드 → native code로 변환 및 실행
- **Interpreter** : 메모리에 로드된 바이트코드 한줄씩 해석 및 실행
- **JIT(Just-In-Time) Compiler** : hot method의 바이트코드 → native code로 컴파일
    - 중간 코드 생성 → 코드 최적화 → native code 생성

<aside>
💡 hot method : 자주 호출되는 메서드

</aside>

- **Garbage Collector(GC)** : 메모리에서 사용하지 않는 개체 식별 및 삭제 (heap)
    - 데몬 스레드로 동작 (즉시 실행x)
- 필요한 경우 JNI로 native method library 호출

### JNI

- Java Native Interface
- native library 사용을 위한 인터페이스
- JVM 내 Java 코드 ↔ native 언어 / 라이브러리 (상호운용 가능)
- Java VM에 의존적이지 x

### Native Method Library

- native 언어 or 어셈블리어 같은 언어로 작성된 native method를 포함한 library
- JVM에서 호출시 JNI로 로딩

## 컴파일러와 인터프리터

- **컴파일러**
    - 프로그래밍 언어 코드 → 타겟 언어로 번역
    - 주로 high-level 언어 → low-level 언어 (assembly, obj code, machine code)
    - 주로 전처리, 어휘/구문/의미 분석, optimization, 기계어 생성 등도 수행
- **인터프리터**
    - 읽은 코드 직접 분석 & 실행

## Java 코드 실행 방식

- Java : 하이브리드 모델
- javac로 소스코드 → 중간 코드(obj code)로 변환 (.java → .class)
- JVM이 바이트코드 분석 & 실행

# [2] 클래스 로더와 클래스 로딩

## Class Loader

![Untitled](https://file.notion.so/f/s/b2f93c52-a19d-4fe6-b489-4c3eb5582882/Untitled.png?id=18ede5f5-d3c6-4668-a7bb-4b29f62eefe2&table=block&spaceId=98c015d1-def8-4399-931a-47453151ae6d&expirationTimestamp=1692158400000&signature=LjgOmpSqgKQz3ydKXyRYFvObPPPwEDnYnO-AJfBOGXw&downloadName=Untitled.png)

- 런타임에 Bytecode → JVM 메모리로 동적 로딩
    - Loading (Creating)
    - Linking : Verity → Prepare → Resolve
    - Initialization
- **필요할 때** 필요한 클래스 / 리소스 로딩
- 클래스 로더는 계층 구조 : Bootstrap → Extension → System

![Untitled](https://file.notion.so/f/s/271bda13-426c-42e7-8f04-08ace11c61a3/Untitled.png?id=4af6ae85-87f5-4558-9a01-a115664577a2&table=block&spaceId=98c015d1-def8-4399-931a-47453151ae6d&expirationTimestamp=1692158400000&signature=GKU28KRFR2edsuO37bpIoBeH9E8i6fU_PAmffuVew64&downloadName=Untitled.png)

- Java9부터 Extension ClassLoader → Platform ClassLoder로 대체

### (참고) JDK 17 문서

- **Bootstrap Class Loader**
    - 최상위 클래스 로더
    - native code로 작성된 클래스 로더(JVM이 로딩)로 base 모듈 로딩
    - `java.lang.ClassLoader`를 로드 : 다른 Java 클래스 로딩 담당
- **Platform Class Loader**
    - Java SE Platform의 모든 API / 클래스 로딩
    - JDK 9 버전부터 일부 메커니즘 변경 : 확장 디렉토리에서 코드 로딩
- **System Class Loader**
    - Java 앱 레벨의 클래스 로딩
    - classpath, module path에 있는 클래스 로딩

## Class Loader 원칙 (Principle)

- **위임** (Delegation Model)
    - 클래스/리소스 등을 자신이 찾기 전에 먼저 상위 클래스 로더에게 위임
    - 상위 클래스가 찾지 못하면 요청한 클래스 로더가 찾아서 로딩 (반복)
- **유일성** (Unique Classes)
    - 상위 클래스 로더가 로딩한 클래스를 하위 클래스 로더가 다시 로딩 방지
    - Delegation 원칙으로 확보 쉬움
- **가시성** (Visibility)
    - 하위 클래스 로더는 상위 클래스 로더가 로딩한 클래스 볼 수 있음
    - 반재로는 성립 x

<aside>
👍🏻 좋은 클래스 로더의 속성

- 같은 클래스명 → 클래스 로더가 같은 클래스 객체 반환
- 하위 → 상위 클래스 로더로 위임한 경우 : 동일한 객체 반환해야함
- Prefetching 시 로딩 에러 발생 가능성이 있어도 ‘적시’에 발생해야함
    - 사용자 정의 클래스 로더가 클래스 바이너리를 미리 가져올때
    - 사용 빈도가 높다고 예상되거나 종속성이 있다고 판단되는 경우
    - 해당 클래스 관련 클래스들을 같이 로딩하는 경우 (그룹 로딩)
</aside>

<aside>
☝🏻 Class Loader 예시

- Java 코드

```java
public static void main(String[] args) {
	System.out.println("Classloader of ArrayList: " + ArrayList.class.getClassLoader());
	System.out.println("Classloader of DriverManager: " + DriverManager.class.getClassLoader());
	System.out.println("Classloader of this class: " + Example03.class.getClassLoader());
}
```

- 출력 결과

```
Classloader of ArrayList: null
Classloader of DriverManager: jdk.internal.loader.ClassLoaders$PlatformClassLoader@1996cd68
Classloader of this class: jdk.internal.loader.ClassLoaders$AppClassLoader@42110406
```

차례대로 Bootstrap → Platform → App Class Loader

</aside>

## Loading

![Untitled](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FzFlOO%2FbtsffuSXqWC%2FbAT6NHuRPPR7x8zmsgmuU0%2Fimg.png)

- 특정 이름을 가진 클래스의 바이트코드를 찾아 클래스를 만드는 프로세스
- FQCN : Fully Qualified Class Name

### ClassNotFoundException

- FQCN으로 로딩했을때 classpath에서 클래스 못찾았을때 발생

### NoClassDefFoundError

- static 블록 실행이나 static 변수 초기화 → 예외 발생했을 때 에러

## Linking

- loading된 클래스 결합
- 완전히 로딩 이후 링킹
- 완전히 검증 이후 초기화
- 링킹 중 오류는 링킹 필요한 시점에서 발생

### Verification

- 바이트코드 유효성 검사
- 유효하지 않으면 VerifyError

### Preparation

- static 필드 생성
- 필요한 메모리 할당
- 기본값으로 초기화

<aside>
💡 **Run-Time Constant Pool**

- 메서드 영역에 할당
- static 상수 & 심볼릭 레퍼런스 포함
</aside>

### Resolution

- 심볼릭 레퍼런스 동적으로 결정

## Initialization

- 클래스, 인터페이스 초기화
- 이전 작업 완료 후 수행
- 초기화 조건
    - 인스턴스 생성
    - static 필드 호출 및 사용
    - 리플렉션으로 메서드 호출
    - 서브 클래스 초기화

### Synchronization

- 멀티스레드 환경 → 초기화 시 동기화 필요
- 동시적인, 재귀적인 초기화 주의

# [3] Java 바이트코드와 코드 캐시

- machine code : cpu 제어하는 기계어
- binary code : 0 & 1
- object code : 컴파일러가 생성한 중간언어

## Java 바이트코드

![Untitled](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FtCsgB%2Fbtsfehm4IIW%2FzkGmCZvM8vbZWt7pwwK2bK%2Fimg.png)

- Java 컴파일러가 생성 (JDK)
- JVM 인식 가능

## Java 코드 캐시

- JVM이 Java 바이트코드 컴파일
→ 네이티브 코드 저장하는 메모리 영역
- JIT 컴파일러가 가장 많이 사용
- 고정된 크기, 확장x
- 가득 차면 추가적으로 컴파일x

### 세그먼트

- non-profiled : 수명이 긴 최적화된 코드
- profiled-code : 수명이 짧은 덜 최적화된 코드
- non-method : JVM 내부 관련 코드(바이트코드 인터프리터)

# [4] 바이트코드를 컴파일하는 AOT, JIT 컴파일러

## Interpreter

- 프로그램 실행 시작할 때
- bytecode 한줄씩 읽음 → 기계어로 변환
- 자체 속도 느림

## AOT 컴파일러

- Ahead of Time
- 앱 실행전 모두 한번에 컴파일 (ex. C언어)
- 프로그램 실행 플랫폼과 프로세서 아키텍쳐에 맞는 실행 코드 얻기 위함
- Java 바이트코드(.class) → AOT 컴파일(joatc)
- 부팅시간↓ 메모리 사용량↓

## JIT 컴파일러

![Untitled](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2F5dhLF%2FbtsftGlCgc9%2Fkfh9Z9BzH8ZxC79KkaLqk0%2Fimg.png)

- Just In Time
- Hot Method : Profile-Guided Optimization(PGO)
    - 자주 쓰이는 부분, 최적화 관련 결정
- `C1` : 런타임에 바이트코드 → 기계어
    - Client Compiler
    - 빠른 시작 & 최적화 (앱)
- `C2` : 기계어 → 네이티브 코드
    - Server Compiler
    - 오랫동안 실행 (서버)
- 플랫폼 확장성↑ 최적화↑
# TDD로 다시 보는 편의점

## 🎯 TDD가 제공한 실질적 이점

---

## 1. 요구사항 탐색 도구로서의 TDD

### 프로모션 계산 로직의 복잡한 케이스 사전 발견

#### 테스트 먼저 작성

```java

@Test
void 투플러스원_프로모션_혜택_계산() {
    Promotion promotion = new Promotion(
            "탄산2+1", 2, 1,
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 12, 31)
    );

    assertThat(promotion.calculateFreeCount(3)).isEqualTo(1);
    assertThat(promotion.calculateFreeCount(5)).isEqualTo(1);
    assertThat(promotion.calculateFreeCount(6)).isEqualTo(2);
}
```

- 2+1 프로모션이면 3개 구매 시 1개 무료는 명확하다
- 그러면 5개 구매하면? 6개 구매하면?

#### 테스트 작성 중 추가 케이스 발견

- 5개 구매 시: 3개만 프로모션 적용 (1세트), 2개는 정가
- 6개 구매 시: 6개 전부 프로모션 적용 (2세트), 2개 무료
- 7개 구매 시: 6개만 프로모션 적용, 1개는 정가     
  → 세트 단위로 끊어서 계산해야 함을 테스트 작성 과정에서 발견!

> **→ 세트 단위로 끊어서 계산해야 함을 테스트 작성 과정에서 발견!**

```java
public int calculateFreeCount(int purchaseQuantity) {
    int setSize = buy + get;  // 2+1이면 3
    int setCount = purchaseQuantity / setSize;  // 5/3 = 1세트
    return setCount * get;  // 1세트 * 1개 = 1개 무료
}
```

---

### 프로모션 재고 부족 시 정가 결제 케이스 발견

#### 테스트 작성 중 의문 발생

```java

@Test
void 프로모션_재고_부족시_정가_결제_확인() {
    Product limitedCola = new Product("콜라", 1000, 7, 10,
            new Promotion("탄산2+1", 2, 1,
                    LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31))
    );

    PurchaseResult result = calculator.calculate(limitedCola, 10, today);
}
```

- 프로모션 재고 7개인데 10개 구매하면?
- 7개 전부 프로모션? 아니면 6개만?
- **요구사항 재확인: "프로모션 재고 내에서만 혜택 적용"**

#### 테스트로 정확한 로직 확인

```java

@Test
void 프로모션_재고_부족시_정가_결제_확인() {
    PurchaseResult result = calculator.calculate(limitedCola, 10, today);

    // 6개는 프로모션 (4개 결제 + 2개 무료)
    // 4개는 정가
    assertThat(result.requiresFullPrice()).isTrue();
    assertThat(result.getFullPriceQuantity()).isEqualTo(4);
    assertThat(result.getPayQuantity()).isEqualTo(8);  // 4 + 4
    assertThat(result.getFreeQuantity()).isEqualTo(2);
}
```

- 7개 중 6개만 프로모션 적용 가능 (2세트 완성)
- 1개는 세트 미완성으로 프로모션 불가
- 나머지 4개(10-6)는 정가 결제

> **→ 테스트를 먼저 작성하며 "세트 단위 완성"이라는 핵심 규칙을 사전에 발견!**

---

### 멤버십 할인 대상 금액 범위 발견

#### 테스트 작성 중 의문 발생

- 콜라 3개 (2+1): 2000원 결제 (1000원은 프로모션)
- 에너지바 5개: 10000원 (프로모션 없음)
- **멤버십 할인은 어디에 적용되지?**

#### 테스트로 정확한 로직 확인

```java

@Test
void 프로모션_미적용_금액만_할인() {
    // 프로모션 미적용 금액 = 10000원 (에너지바만)
    // 멤버십 할인 = 3000원 (10000 * 0.3)

    int discount = calculator.calculateDiscount(purchases);

    assertThat(discount).isEqualTo(3000);
}
```

> **→ 테스트 작성 과정에서 "프로모션 적용 여부에 따른 할인 대상 구분"이라는 숨은 요구사항 발견!**

---

### 배운 점

#### 테스트 작성 = 요구사항 명확화

- "프로모션 재고 부족 시 어떻게 처리하지?"라는 모호한 요구사항이 테스트 작성 과정에서 구체화됨
- 세트 단위 계산, 정가 결제 확인, 멤버십 할인 대상 등 **숨은 규칙들을 사전에 발견**

#### 작은 단위로 검증하며 큰 실수 방지

- 프로모션 계산 → 재고 차감 → 멤버십 할인 순서로 작은 단위 테스트
- 각 단계에서 엣지 케이스를 발견하고 처리

#### "이렇게 동작하겠지" → "테스트로 확인해보자"

- 구현 전에 테스트로 동작을 명확히 정의
- **가정이 아닌 검증을 통한 요구사항 이해!**

---

## 2. 설계 피드백 도구로서의 TDD

### Product와 Promotion 관계 설계

#### 테스트 먼저 작성

```java

@Test
void 프로모션이_있는_상품_생성() {
    Promotion promotion = new Promotion("탄산2+1", 2, 1, ...);

    Product product = new Product("콜라", 1000, 10, 10, promotion);
}
```

- Product가 Promotion을 알아야 하나?

#### 설계 고민

- Product가 Promotion을 소유? → 1:1 관계
- 별도 매핑 테이블? → 복잡도 증가
- Promotion이 Product 목록 소유? → 역방향 참조

#### 테스트로 자연스러운 관계 도출

```java

@Test
void 상품은_프로모션_적용_여부를_알_수_있다() {
    assertThat(product.hasPromotion()).isTrue();
    assertThat(product.getPromotion()).isEqualTo(promotion);
}

@Test
void 프로모션_없는_상품도_존재한다() {
    Product water = new Product("물", 500, 0, 10);

    assertThat(water.hasPromotion()).isFalse();
}
```

- "상품이 프로모션을 가지고 있는가?"라는 질문이 자연스러움
- **Product가 Optional하게 Promotion을 소유하는 구조로 결정!**

---

### PurchaseCalculator 객체의 필요성 발견

#### 테스트 작성 중 의문 발생

```java

@Test
void 정상_프로모션_적용() {
    // 3개 구매 → 2개 결제, 1개 무료
    // 이 계산을 누가 해야 하지?
}
```

#### 설계 고민

- Product가 계산? → Product가 Promotion 로직까지 알아야 함
- Promotion이 계산? → Promotion이 재고까지 알아야 함
- Controller가 계산? → 비즈니스 로직이 Controller에 위치

> **⇒ 해당 책임을 가질 새로운 객체가 필요하다!**

#### 테스트로 새로운 객체 도출

```java

@Test
void 정상_프로모션_적용() {
    PurchaseCalculator calculator = new PurchaseCalculator();

    PurchaseResult result = calculator.calculate(colaWith2Plus1, 3, today);

    assertThat(result.getPayQuantity()).isEqualTo(2);
    assertThat(result.getFreeQuantity()).isEqualTo(1);
}
```

- `calculate`라는 메시지가 필요하다
- Product와 수량, 날짜를 받아서 결과를 반환한다
- **PurchaseCalculator라는 서비스 객체가 자연스럽게 도출!**

---

### PurchaseResult 객체의 필요성 발견

#### 테스트 작성 중 필요한 정보 증가

```java

@Test
void 프로모션_수량_미달_시_추가구매_제안() {
    PurchaseResult result = calculator.calculate(colaWith2Plus1, 2, today);

    // 결과에 뭘 담아야 하지?
    // - 결제 수량?
    // - 무료 수량?
    // - 추가 구매 제안 여부?
    // - 제안 메시지?
}
```

#### 설계 고민

- 처음에는 단순히 `(payQuantity, freeQuantity)`만 반환하려 했음
- 테스트 케이스를 추가하며 **필요한 정보가 계속 늘어남**
- 추가 구매 제안, 정가 결제 확인 등 복잡한 상태 관리 필요

#### Builder 패턴 적용 필요성 발견

```java
public static class Builder {
    private String productName;
    private int requestedQuantity;
    private int payQuantity;
    private int freeQuantity;
    private boolean suggestAddition = false;
    private int suggestedAddition = 0;
    private boolean requiresFullPrice = false;
    private int fullPriceQuantity = 0;

    // ...
}
```

> **→ 테스트 작성 과정에서 파라미터가 계속 늘어나는 것을 발견하고 Builder 패턴 도입 결정!**

---

### 배운 점

#### 테스트 작성이 메시지를 발견

- "calculate라는 메시지가 필요하다" → **PurchaseCalculator 탄생**
- "결과를 담을 객체가 필요하다" → **PurchaseResult 탄생**
- "상품이 프로모션을 아는가?" → **관계 설계 결정**

#### 테스트하기 어려운 코드 = 설계 문제 신호

- 파라미터가 너무 많아짐 → Builder 패턴 적용
- 계산 로직이 여러 곳에 분산 → 서비스 객체로 응집

#### 점진적 설계 발전

**점진적 설계 발전**

- 1단계: "그냥 메서드로 계산하면 되지"
- 2단계: "테스트하려니 여러 정보가 필요하네"
- 3단계: "별도 객체로 분리해야겠다"
- 4단계: "Builder 패턴으로 생성을 단순화하자 !

---

## 3. 리팩토링 안전망으로서의 TDD

### 재고 차감 로직 리팩토링

#### 버그 사전에 미리 발견

```java

@Test
void 프로모션_재고_소진_후_일반_재고_차감() {
    Product product = new Product("콜라", 1000, 3, 10, promotion);

    product.deductStock(5);

    assertThat(product.getPromotionStock()).isEqualTo(0);
    assertThat(product.getRegularStock()).isEqualTo(8);
}
```

- **테스트 실패!** 프로모션 재고가 0이 아니라 3으로 남아있음
- 기존 로직은 프로모션 재고가 부족하면 일반 재고에서만 차감

> **→ 테스트가 있었기에 버그를 즉시 발견하고 안전하게 수정!**

---

### PurchaseCalculator 대대적 리팩토링

#### 리팩토링 전: 100줄 이상의 복잡한 로직

```java
public PurchaseResult calculate(Product product, int requestQuantity, LocalDate date) {
    // 프로모션 체크
    // 추가 구매 제안 체크
    // 정가 결제 체크
    // 정상 프로모션 적용
    // 100줄 이상의 복잡한 로직...
}
```

#### 리팩토링 후: 단계별 분리

```java
public PurchaseResult calculate(Product product, int requestQuantity, LocalDate date) {
    if (!product.hasPromotion() || !product.getPromotion().isAvailable(date)) {
        return createNormalPurchase(product, requestQuantity);
    }

    // 단계 1: 추가 구매 제안 체크
    PurchaseResult suggestionResult = checkSuggestion(product, requestQuantity, ...);
    if (suggestionResult != null) return suggestionResult;

    // 단계 2: 프로모션 재고 부족 체크
    PurchaseResult fullPriceResult = checkFullPriceRequired(product, requestQuantity, ...);
    if (fullPriceResult != null) return fullPriceResult;

    // 단계 3: 정상 프로모션 적용
    return applyNormalPromotion(product, requestQuantity, ...);
}
```

> **→ 매 단계마다 테스트가 동작 보장, 과감한 구조 변경 가능!**

---

### 파일 파싱 로직 - 같은 상품 두 줄 합치기

#### 문제 인식

```java

@Test
void 상품_파일_파싱() {
    Map<String, Product> products = parser.parseProducts(content, promotionMap);

    Product cola = products.get("콜라");
    assertThat(cola.getPromotionStock()).isEqualTo(10);
    assertThat(cola.getRegularStock()).isEqualTo(10);
}
```

**products.md 파일:**

- 콜라, 1000, 10, 탄산2+1 → 프로모션 재고
- 콜라, 1000, 10, null → 일반 재고

**같은 상품이 두 줄인데 어떻게 합치지?**

#### ProductBuilder 패턴으로 해결

- 테스트가 "두 줄을 하나의 Product로 합쳐야 한다"는 요구사항을 명확히 함
- **Builder 패턴으로 점진적 구성 가능**

---

### 프로그래밍 요구사항 적용을 위한 최종 리팩토링

#### 컨트롤러 계층

```java
public class StoreController {

    // ... 메서드 분리로 depth 2 이내, else 제거, 메서드 10줄 이내 달성
}
```

#### 도메인 서비스 계층

**MembershipCalculator**

```java
public class MembershipCalculator {
// ... 메서드 분리로 depth 2 이내, else 제거, 메서드 10줄 이내 달성
}
```

**PurchaseCalculator**

```java
public class PurchaseCalculator {

// ... 메서드 분리로 depth 2 이내, else 제거, 메서드 10줄 이내 달성
}
```

#### View 계층

**InputView**

```java
public class InputView {
// ... 메서드 분리로 depth 2 이내, else 제거, 메서드 10줄 이내 달성
}
```

---

### 배운 점

**TDD 사이클에 리팩토링이 자연스럽게 포함**

- Red → Green → **Refactor**
- 매 사이클마다 구조 개선 기회
- 작은 단위로 점진적 리팩토링

**테스트가 리팩토링의 안전망**

- "이렇게 바꿔도 될까?" → 테스트 통과! 안전하다!
- 대담한 구조 변경 가능
- 기존 테스트 유지 = 동작 보장

**심리적 안정 제공**

- 테스트 없이는 "수정하면 깨질까봐" 두려움
- 테스트 있으면 "일단 바꿔보고 확인하자" 자신감
- 구조 변경의 족쇄가 아닌 안전망!

**점진적 리팩토링 과정**

- 1단계: 긴 메서드 → 테스트 통과 확인
- 2단계: 메서드 추출 → 테스트 통과 확인
- 3단계: 클래스 분리 → 테스트 통과 확인
- 4단계: 패턴 적용 → 테스트 통과 확인

---

## 🤔 어떤 고민이 있었고, 왜 그런 결정을 했는지

---

## 1. View에서 Domain 참조 - DTO 도입 vs 직접 참조

### 문제 인식

```java
private static void printProduct(Product product) {
    if (product.hasPromotion() && product.getPromotionStock() > 0) {
        System.out.printf("- %s %,d원 %d개 %s%n",
                product.getName(),
                product.getPrice(),
                product.getPromotionStock(),
                product.getPromotion().getName()
        );
    }
}
```

- View가 Domain 객체(Product, Promotion)를 직접 알고 있음
- `product.getPromotion().getName()` 같은 체이닝 호출
- Domain 내부 구조 변경 시 View도 수정 필요

> **→ View가 Domain에 의존하면 안 좋은데 DTO를 도입할지 고민**

---

### DTO 도입 검토

**장점**

- View와 Domain 분리
- Domain 변경 시 변환 로직만 수정
- 명확한 계층 구조

**단점**

- DTO 클래스 추가 (ProductDisplayDTO, ReceiptDTO 등)
- 변환 로직 작성 필요
- 코드량 증가
- 결국 Domain이 변경되면 DTO도 변경해야 함

---

### 현재 프로젝트 상황 분석

**프로젝트 특성**

- 단순 콘솔 프로그램
- API 없음 (JSON 변환 불필요)
- View가 단순 출력만 담당
- Domain 구조 변경 가능성 낮음

**DTO 도입 시 실제 이득은?**

- View와 Domain 분리?
- 리포지토리와 외부 시스템 연동이 없으니 서비스 계층 X
- 어차피 DTO 변환 로직에서 Domain 의존
- 변경 격리? → 현재 단순 콘솔 프로그램에서 변경 파급 효과 미미

> **→ 이 규모에서 DTO는 오버엔지니어링이 확실하다! 득보다 실이 크다!**

---

### 최종 결정: DTO 없이 방어적 복사 적용

#### 프로젝트 규모에 맞는 설계

- 단순 콘솔 프로그램에 DTO는 과도함
- **YAGNI 원칙**: 지금 필요 없으면 만들지 마라

#### 진짜 문제 해결에 집중

- View-Domain 의존성보다 **불변성 문제**가 더 시급
- 외부에서 내부 상태 변경 가능한 것이 실제 위험

#### 방어적 복사로 충분히 안전

- getter는 조회 용도로 허용
- 컬렉션은 불변 반환으로 보호
- 캡슐화 유지하면서 단순함 유지

---

### 배운 점: Getter 사용의 올바른 이해

> **"Getter를 지양하라"의 진짜 의미**

| 잘못된 이해              | 올바른 이해                        |
|---------------------|-------------------------------|
| "getter를 아예 만들지 마라" | "getter로 꺼내서 외부에서 로직 처리하지 마라" |

**조회/출력을 위한 getter는 자연스러운 사용!**

---

## 2. 더 나은 설계의 환상 - YAGNI와 TDD의 본질

### ConvenienceStore의 정체성과 책임 고민

```java
public class ConvenienceStore {
    private final Map<String, Product> products;  // 저장소 역할?
    private final PurchaseCalculator calculator;

    public PurchaseContext processPurchase(...) {
        validateProducts(items);      // 검증

        for (OrderItem item : items) {
            Product product = products.get(...);  // 조회

            if (!product.canPurchase(...)){      // 비즈니스 로직
                throw new IllegalArgumentException(...);
            }

            PurchaseResult result = calculator.calculate(...);  // 계산 위임
            context.addPurchase(...);  // 컨텍스트 관리
        }

        return context;
    }
}
```

**ConvenienceStore가 하는 일이 너무 많아 보임**

- 저장소 역할 (products Map 관리)
- 검증 로직
- 비즈니스 로직 조율
- 컨텍스트 생성 및 관리

> "이건 도메인 객체인가? 서비스인가? 저장소??"
> "책임이 너무 많지 않나? 분리해야 하는 것 아닌가?"

---

### Repository 도입 고려

**장점**

- 책임 분리 명확
- 테스트 시 Repository Mock 가능
- 추후 DB 연동 용이

하지만

- 지금 파일 읽기도 잘 되는데?
- 인터페이스가 지금 필요한가?
- 구현체가 하나뿐인데 추상화?

### 애플리케이션 서비스 계층 도입 고려

장점

- 계층이 명확해짐
- Controller가 단순해짐

하지만

- 이게 정말 나아진 건가?
- 코드가 오히려 더 복잡해진 것 같은데?
- PurchaseService와 ConvenienceStore 역할이 겹치는데?

---

### PurchaseContext의 역할과 사용 방식 고민

```java
private void processPurchase() {
    // Service가 생성
    PurchaseContext context = store.processPurchase(items, today);

    // Controller가 직접 수정
    handlePromotionQuestions(context);
    context.applyMembershipDiscount(apply);

    // Service에 다시 전달
    store.updateStock(context);
}
```

- PurchaseContext가 Controller에서 직접 수정됨
- Controller가 context를 직접 수정해도 되나?
- 엔티티 역할 같은데 이렇게 막 돌아다녀도 되나?

---

### 현재 상황 분석

- 테스트 충분히 가능, 파일 I/O 잘됨, 순수 메모리 연산, DB 연동 예정 아님.
    - **실제 문제 없음**
        - 고전파 테스트로 충분히 검증가능
        - 지금 필요하지 않으면 만들지 마라
        - DB 연동할 때 그때 Repository 도입
    - **단순함 유지**
        - 복잡한 추상화보다 명확한 구조
        - 읽기 쉽고 이해하기 쉬운 코드
- 리포지토리가 필요한 경우
    - 파일 I/O 추가 시, DB 연동 시, API 호출 시
- PurchaseContext
    - 불안의 원인
        - 엔티티는 아무렇게 수정하고 돌아다니면 안되지 않나?
        - 완벽한 캡슐화에 대한 집착
    - 하지만 현재 상황
        - PurchaseContext는 일시적 객체
        - DB에 저장되지 않음

---

### 깨달음: 스스로에게 다시 묻고 답한 질문

| 질문                          | 답변                             |
|-----------------------------|--------------------------------|
| 현재 코드에 실제 버그가 있나?           | **없음.** 모든 테스트 통과하고 프로그램 이상 없음 |
| 현재 코드가 읽기 어려운가?             | **아니.** 오히려 간단하고 충분히 명확함       |
| 현재 코드가 변경하기 어려운가?           | **아니.** 테스트가 있어서 안전하게 변경 가능함   |
| 외부 의존성 추가 예정인가? (DB, API 등) | **없음.** 학습용 프로젝트!              |
| **그럼 왜 리팩토링하려고 하는가?**       | **음…………**                      |

---

### 진짜 이유 발견

> **나는 실제 문제를 해결하려는 게 아니었다!!!!!**

**내가 원했던 것**

- "그냥 더 객체지향"으로 보이고 싶었다
- "전문적인" 코드를 작성하고 싶었다
- 배운 패턴들을 "써먹고" 싶었다
- "완벽한" 구조를 만들고 싶었다

**하지만 현실**

- 테스트는 이미 충분히 잘 되고 있었다
- 코드는 이미 충분히 읽기 쉬웠다
- 구조는 이미 충분히 명확했다
- **문제는 없었다!!!!!**

---

### 다시 고려해보기

**문제 1: ConvenienceStore의 책임이 많다**

**문제 2: 계층 분리가 명확하지 않다**

- 현재 : 저장소 + 비즈니스 로직
- 문제 : 책임 분리 필요(?)
- 실제 문제 없음 !
    - DB 연동 예정없음
    - 파일 읽기 잘됨
    - 테스트도 잘 됨
- 이론적 문제일 뿐 !
    - Web API 만들 예정 아님
    - 현재 구조로 테스트 충분히 가능
    - 코드 이해하기 충분히 쉽다.

---

### 배운 점
- 문제가 없다면 해결하지 마라. → 실제 문제만 해결하라
    - 실제 문제 : 버그, 성능 이슈, 이해하기 어려움
    - 이론적 문제 : 더 객체지향적으로 !, 더 많은 추상화 !
    - 이 코드는 개선 할수 있어? X
    - 이 코드는 문제가 있어? O
- 리팩토링 타이밍
    - X  = 더 복잡하게 만들기, 패턴 적용하기, 추상화 추가추가하기
        - 나중을 위해 미리, 이론적으로 더 좋은 방법이 있어어서 X
    - O  = 중복 제거, 의도 명확히 하기, 가독성 좋고 가독성 좋게 하기
        - 중복이 보일때, 이해하기 어려울때
        - 새로운 기능 추가가 어려울 때
        - 실제 요구 사항이 생길떄
- TDD 학습의 본질
    - Red-Green-Refactor 사이클 이해
    - 테스트 작성 능력 향상
    - 리팩토링 감각 기르기
    - X : 모든 디자인 패턴 적용, 완벽한 아키텍처 설계
- "더 나은 설계"를 추구하다가 "충분히 좋은 설계"를 놓칠 뻔했다







package store.view

class CustomerOutput(private val out: CommonOutput) {

    fun showLoginMenu() {
        out.printMessage("\n=== 구매자 로그인 ===")
        out.printMessage("1. 회원가입")
        out.printMessage("2. 로그인")
        out.printMessage("3. 비회원으로 계속")
        out.printInline("선택: ")
    }

    fun showSignUpForm() {
        out.printMessage("\n=== 회원가입 ===")
    }

    fun askName() {
        out.printInline("이름을 입력해주세요: ")
    }

    fun askPhoneNumber() {
        out.printInline("휴대폰 번호를 입력해주세요 (예: 010-1234-5678): ")
    }

    fun askPassword() {
        out.printInline("비밀번호를 입력해주세요 (8자 이상, 영문+숫자+특수문자): ")
    }

    fun askPasswordConfirm() {
        out.printInline("비밀번호를 다시 입력해주세요: ")
    }

    fun showSignUpSuccess(name: String) {
        out.printMessage("\n✓ 회원가입이 완료되었습니다. 환영합니다, ${name}님!")
    }

    fun showLoginForm() {
        out.printMessage("\n=== 로그인 ===")
    }

    fun showLoginSuccess(name: String) {
        out.printMessage("\n✓ 로그인 성공! 환영합니다, ${name}님!")
    }

    fun showLoginFailed() {
        out.printMessage("\n✗ 로그인 실패. 휴대폰 번호 또는 비밀번호가 일치하지 않습니다.")
    }

    fun askContinueShopping() {
        out.askYesOrNo("감사합니다. 구매하고 싶은 다른 상품이 있나요?")
    }

    fun askMembershipDiscount() {
        out.askYesOrNo("멤버십 할인을 받으시겠습니까?")
    }

    fun askConfirmCheckout() {
        out.askYesOrNo("결제를 진행하시겠습니까?")
    }

    fun showCartModifyMenu() {
        out.printMessage("\n=== 장바구니 수정 ===")
        out.printMessage("1. 수량 변경")
        out.printMessage("2. 상품 제거")
        out.printMessage("3. 장바구니 비우기")
        out.printInline("선택: ")
    }

    fun askModifyCart() {
        out.askYesOrNo("장바구니를 수정하시겠습니까?")
    }

    fun askBuyWithoutPromotion(productName: String, quantity: Int) {
        out.askYesOrNo("현재 ${productName} ${quantity}개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까?")
    }

    fun askAddFreeItem(productName: String) {
        out.askYesOrNo("현재 ${productName}은(는) 1개를 무료로 더 받을 수 있습니다. 추가하시겠습니까?")
    }

    fun showPurchaseInputGuide() {
        out.printMessage("\n구매하실 상품명과 수량을 입력해주세요. (예: [사이다-2],[감자칩-1])")
        out.printInline("입력: ")
    }

    fun askViewDetailHistory() {
        out.askYesOrNo("구매 이력 상세보기를 하시겠습니까?")
    }

    fun askViewStatistics() {
        out.askYesOrNo("카테고리별 통계를 보시겠습니까?")
    }

    fun askHistoryId() {
        out.printInline("조회할 주문번호를 입력하세요: ")
    }
}
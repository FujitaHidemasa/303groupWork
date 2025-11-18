// ▼ サーバーから埋め込まれた住所一覧（ID → 住所文字列）
const addressMap = /*[[${addresses}]]*/ {};
// ↑ thymeleaf で JS に住所一覧を埋め込むためのテンプレート
// controller から渡された addresses を使う


// ------------------------------------------------------
// ▼ 配送先住所の切り替え（登録住所・手入力）
// ------------------------------------------------------
function toggleManualAddress() {
    const select = document.getElementById("addressSelect");
    const manualBox = document.getElementById("manualAddressBox");
    const finalAddress = document.getElementById("finalAddress");

    // 手入力モード
    if (select.value === "manual") {
        manualBox.style.display = "block";
        finalAddress.value = ""; 
        return;
    }

    // 通常の登録住所モード
    manualBox.style.display = "none";

    const selectedId = select.value;

    // ID → テキスト住所に変換
    finalAddress.value = addressMap[selectedId] || "";
}


// ------------------------------------------------------
// ▼ 手入力フォームの内容を反映する
// ------------------------------------------------------
function syncManualAddress() {
    const manualInput = document.getElementById("manualAddressInput");
    const finalAddress = document.getElementById("finalAddress");
    finalAddress.value = manualInput.value;
}

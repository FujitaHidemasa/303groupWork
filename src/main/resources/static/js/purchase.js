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
    const manualInput = document.getElementById("manualAddressInput");

    if (!select || !manualBox) {
        return;
    }

    // 手入力モード
    if (select.value === "manual") {
        manualBox.style.display = "block";
    } else {
        // 登録住所モード → 手入力欄は隠して中身もクリア
        manualBox.style.display = "none";
        if (manualInput) {
            manualInput.value = "";
        }
    }
}


// ------------------------------------------------------
// ▼ 手入力フォームの内容を反映する
// ------------------------------------------------------
function syncManualAddress() {
    const manualInput = document.getElementById("manualAddressInput");
    const finalAddress = document.getElementById("finalAddress");
    finalAddress.value = manualInput.value;
}

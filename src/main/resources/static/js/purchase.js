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

    // 「直接入力する」を選んだときだけ入力欄を表示
    if (select.value === "manual") {
        manualBox.style.display = "block";
        if (manualInput) {
            manualInput.focus();
        }
    } else {
        // それ以外は非表示＆入力値はそのままでも OK
        manualBox.style.display = "none";
    }
}

// ------------------------------------------------------
// フォーム送信前の必須チェック
// 「お届け先」が選択されているか、
// 「直接入力する」の場合は住所が入っているかを確認
// ------------------------------------------------------
function validatePurchaseForm() {
    const select = document.getElementById("addressSelect");
    const manualInput = document.getElementById("manualAddressInput");

    if (!select) {
        // 要素自体がなければ何もしない
        return true;
    }

    const value = select.value;

    // 1) 何も選ばれていない
    if (!value) {
        alert("お届け先を選択するか、住所を直接入力してください。");
        return false;
    }

    // 2) 「直接入力する」の場合はテキスト必須
    if (value === "manual") {
        if (!manualInput || manualInput.value.trim() === "") {
            alert("お届け先の住所を入力してください。");
            return false;
        }
    }

    // ここまで来れば OK
    return true;
}

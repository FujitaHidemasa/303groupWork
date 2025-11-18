// ▼ 配送先住所の表示切り替え
function toggleManualAddress() {
    const addressSelect = document.getElementById("addressSelect");
    const manualBox = document.getElementById("manualAddressBox");
    const manualInput = document.getElementById("manualAddressInput");
    const finalAddress = document.getElementById("finalAddress");

    if (addressSelect.value === "manual") {
        manualBox.style.display = "block";
        manualInput.required = true;
        finalAddress.value = "";
    } else {
        manualBox.style.display = "none";
        manualInput.required = false;

        // 通常住所を hidden にコピー
        finalAddress.value = addressSelect.value;
    }
}

// ▼ 手入力の反映
function syncManualAddress() {
    const manualInput = document.getElementById("manualAddressInput");
    const finalAddress = document.getElementById("finalAddress");
    finalAddress.value = manualInput.value;
}

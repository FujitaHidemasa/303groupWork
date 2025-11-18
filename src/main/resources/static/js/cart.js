/**
 * cart.js
 * カート追加処理とポップアップ制御
 */

document.addEventListener("DOMContentLoaded", () => {

    const cartForm = document.getElementById("cartForm");
    const popup = document.getElementById("cartPopup");
    const closePopup = document.getElementById("closePopup");
    const headerCartCount = document.getElementById("cart-count");

    // カート追加処理
    if (cartForm) {
        cartForm.addEventListener("submit", (e) => {
            e.preventDefault();

            fetch(cartForm.action, {
                method: "POST",
                headers: { "X-Requested-With": "XMLHttpRequest" },
                body: new FormData(cartForm),
                credentials: "same-origin"
            })
                .then(response => {
                    // 未ログイン時のリダイレクト処理
                    if (response.redirected && response.url.includes("/login")) {
                        const currentUrl = window.location.pathname + window.location.search;
                        window.location.href = `/login?next=${encodeURIComponent(currentUrl)}`;
                        return;
                    }

                    if (!response.ok) throw new Error("サーバーエラー");
                    return response.json();
                })
                .then(data => {
                    if (!data) return;

                    if (data.status === "ok") {
                        // カートアイコンの表示更新
                        if (headerCartCount) {
                            headerCartCount.textContent = data.count ?? 0;
                            headerCartCount.style.display =
                                (data.count > 0) ? "inline-block" : "none";
                        }

                        // ポップアップ表示
                        if (popup) {
                            popup.style.display = "flex";
                        }
                    } else {
                        alert("カート追加に失敗しました。");
                    }
                })
                .catch(err => {
                    console.error("カート追加エラー:", err);
                });
        });
    }

    // ポップアップ閉じる処理
    if (closePopup && popup) {
        closePopup.addEventListener("click", () => {
            popup.style.display = "none";
        });

        // オーバーレイ背景をクリックして閉じる
        popup.addEventListener("click", (e) => {
            if (e.target === popup) {
                popup.style.display = "none";
            }
        });
    }
});
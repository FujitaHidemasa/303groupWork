/**
 * お気に入り機能のJavaScript　item/detail.htmlで使用
 */
document.addEventListener("DOMContentLoaded", () => {
    const button = document.querySelector(".favorite-btn");

    if (!button) return; // ボタンが無ければ処理を終了

    button.addEventListener("click", async () => {
        const itemId = button.getAttribute("data-item-id");

        try {
            const response = await fetch(`/items/${itemId}/favorite`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "X-Requested-With": "XMLHttpRequest"
                }
            });

            if (response.ok) {
                const isFavorite = await response.json(); // true または false
                button.textContent = isFavorite ? "★ お気に入り" : "☆ お気に入り";
            } else {
                alert("お気に入りの更新に失敗しました");
            }
        } catch (error) {
            console.error(error);
            alert("通信エラーが発生しました");
        }
    });
});

/**
 * お気に入り機能のJavaScript　item/detail.htmlで使用
 */
document.addEventListener("DOMContentLoaded", () => {
    const button = document.querySelector(".favorite-btn");

    if (!button) return; // ボタンが無ければ処理を終了
	
	// ★追加 11/06 谷口：CSRFメタから取得
	const token = document.querySelector('meta[name="_csrf"]')?.getAttribute("content");
	const headerName = document.querySelector('meta[name="_csrf_header"]')?.getAttribute("content");

    button.addEventListener("click", async () => {
        const itemId = button.getAttribute("data-item-id");

		try {
			// ★修正：POST先を /voidrshop/items/{itemId}/favorite に変更
			const response = await fetch(`/voidrshop/items/${itemId}/favorite`, {
				method: "POST",
				headers: {
					"Content-Type": "application/json",
					...(token && headerName ? { [headerName]: token } : {}), // ★追加：CSRFヘッダ
				},
				body: "{}"
			});

			if (!response.ok) {
				alert("お気に入りの更新に失敗しました");
				return;
			}

			const isFavorite = await response.json(); // true / false（Controllerがboolean返却）
			button.textContent = isFavorite ? "★ お気に入り" : "☆ お気に入り";
		} catch (e) {
			console.error(e);
			alert("通信エラーが発生しました");
		}
	});
});

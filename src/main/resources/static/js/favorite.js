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
				body: "{}",
				credentials: "same-origin", // ★念のためクッキー送信を明示
				redirect: "follow",         // ★リダイレクト検知用に明示（デフォルト）
			});
			
			// 302で /login に飛ばされた場合
			if (response.redirected) {
				const nextRaw = location.pathname + location.search; // ← 生のパス
				const url = new URL(response.url, location.origin);  // /login のはず
				url.searchParams.set("next", nextRaw);               // ← encodeは任せる
				window.location.href = url.toString();
				return;
			}

			// 401/403 の場合
			if (response.status === 401 || response.status === 403) {
				const nextRaw = location.pathname + location.search; // ← 生のパス
				const url = new URL("/login", location.origin);
				url.searchParams.set("next", nextRaw);               // ← encodeは任せる
				window.location.href = url.toString();
				return;
			}
			
			if (!response.ok) {
				const text = await response.text().catch(() => "");
				console.error("favorite toggle error:", response.status, text);
				alert("お気に入りの更新に失敗しました");
				return;
			}

			// ★コンテンツタイプがJSONでない＝ログイン画面HTML等 → /loginへ
			const ct = response.headers.get("content-type") || "";
			if (!ct.includes("application/json")) {
				const next = encodeURIComponent(location.pathname + location.search);
				window.location.href = `/login?next=${next}`;
				return;
			}

			const isFavorite = await response.json(); // true/false
			button.textContent = isFavorite ? "★ お気に入り" : "☆ お気に入り";
		} catch (e) {
			console.error(e);
			alert("通信エラーが発生しました");
		}
	});
});

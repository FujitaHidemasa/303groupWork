document.addEventListener("DOMContentLoaded", () => {
  const modal = document.getElementById("storeModal");
  const playBtn = document.getElementById("playNowBtn");
  const openStoreModal = document.getElementById("openStoreModal");
  const closeBtn = document.querySelector(".close");

  console.log("modal:", modal);     
  console.log("playBtn:", playBtn); 
  console.log("openStoreModal:", openStoreModal);
  console.log("closeBtn:", closeBtn); 

  // ▼ モーダルを開く共通処理
  const openModal = () => {
    if (modal) modal.style.display = "flex";
  };

  // ▼ モーダルを閉じる共通処理
  const closeModal = () => {
    if (modal) modal.style.display = "none";
  };

  // ▼ ヘッダー「今すぐプレイ」
  if (playBtn) {
    playBtn.addEventListener("click", openModal);
  }

  // ▼ HOME「今すぐダウンロード」
  if (openStoreModal) {
    openStoreModal.addEventListener("click", (e) => {
      e.preventDefault();
      openModal();
    });
  }

  // ▼ 閉じる（×）
  if (closeBtn) {
    closeBtn.addEventListener("click", closeModal);
  }

  // ▼ 外を押したら閉じる
  window.addEventListener("click", (e) => {
    if (e.target === modal) closeModal();
  });
});

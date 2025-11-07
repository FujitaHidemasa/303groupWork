

	document.addEventListener("DOMContentLoaded", () => {
	  const modal = document.getElementById("storeModal");
	  const playBtn = document.getElementById("playNowBtn");
	  const closeBtn = document.querySelector(".close");

	  console.log("modal:", modal);     
	  console.log("playBtn:", playBtn); 
	  console.log("closeBtn:", closeBtn); 

	  if(modal && playBtn && closeBtn){
	    playBtn.addEventListener("click", () => {
	      console.log("ボタン押された！");
	      // CSSが適用されていればモーダルが表示される
	      modal.style.display = "flex"; 
	    });
	    closeBtn.addEventListener("click", () => modal.style.display = "none");
	    window.addEventListener("click", e => { if(e.target === modal) modal.style.display = "none"; });
	  }
	});



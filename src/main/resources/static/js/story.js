document.addEventListener("DOMContentLoaded", () => {
  const nextBtn = document.getElementById("nextBtn");
  const prevBtn = document.getElementById("prevBtn");
  const storyContainer = document.getElementById("storyContainer");
  const storyImage = document.getElementById("storyImage");
  const storyText = document.getElementById("storyText");
  const textLayer = document.getElementById("textLayer");
  prevBtn.style.display = "none"; // ← 読み込み時に非表示にする


  const images = [
    "/images/story/story1.png",
    "/images/story/story2.png",
    "/images/story/story3.png",
    "/images/story/story4.png",
    "/images/story/story5.png",
    "/images/story/story6.png"
  ];

  const texts = [
/*6---------------------------------------------------------------------------*/
    `<p>戦う理由 — 虚無の証明
彼らが共鳴場で戦う理由は、もはや「政府の命令」や「リフトの制御」ではなく、
「戦う理由は一つ——自分の“虚無”を証明するため。」という、より個人的で根源的なものだった。
元VEPの制御派と活用派のアバターたちは、エッセンスに対する自らの信念を力として激突させ、「自分の考える現実」が正しいと世界に示そうとする一方、
エッセンスの力が精神的な強さに比例することから、戦いを通じて自らを強化し「最強の虚無」として世界の支配権を確保しようとする。さらに、アバターの能力が内なる「後悔」や「願望」といった虚無の衝動の具現化であるため、彼らは戦いを衝動を解放し、
自己の存在意義を再確認する**「儀式」としても捉えている。こうして、世界は公式な介入から離れ、ヴォイド・アバターたちが、それぞれの「虚無」を抱え、「生き残る者が、現実を決める」という過酷な理念のもと、今日も戦場に立ち続けている。
</p>`,
/*1---------------------------------------------------------------------------*/  
    `<p>物語は、ごく普通の朝、「灰色の海が割れた」衝撃的な出来事から幕を開ける。
午前9時12分。埠頭に冷たい潮風が吹く中、世界各地で観測された圧力異常は、やがて海面を裂く白い“ひだ”となり、空間そのものを開いた。海から噴き上がった色なき光は、まるで「無」を吹き付けるように周囲を凍らせた。
最初に異変に気づいたのは、埠頭で缶コーヒーを啜っていた一人の少年。彼の手にあったリールが磁場に引き寄せられ、彼の世界は引き伸ばされ、翌朝、彼はニュース映像の片隅から“静かに消えた”。
この出来事は 「<span class="void-glow">ファースト・リフト</span>」と呼ばれ、世界は不可逆的な変化を遂げた。裂け目から噴出した未知のエネルギー「ヴォイド・エッセンス」は、触れた物質の法則を書き換え、人の記憶や感情を“実体”に変換する力を秘めていた。
世界は混乱し、各国政府は「止めるか」「使うか」「見るだけでいるか」という、三つの問いに分かれることになった。
 </p>`,
/*2---------------------------------------------------------------------------*/
   `<p>ファースト・リフトから半年後、主要国は国際的な極秘組織「<span class="void-glow">ヴォイド・エージェント計画（VEP）</span>」を設立。目的は「リフトの調査・制御」と「エッセンス適合者（Void Resonant）の確保と管理」であった。
エッセンスへの抵抗力や親和性の高い適合者たちは、希望、あるいは兵器として養成される。
VEPの極秘研究所では、科学者たちが日夜ヴォイド・エッセンスの解析に明け暮れていた。中央には、特殊な封印装置の中に浮かぶ、淡い紫色のエッセンス。
研究初期は「エッセンスの無力化」を目指したが、一切の物理干渉を拒否し、安全隔壁を歪ませる不可解な現象が頻発。しかし、研究が進むにつれ、エッセンスが特定の精神エネルギーに反応することが判明する。
適合者たちはこのエネルギーを無意識に発しており、彼らの脳波パターンがエッセンスの挙動に影響を与えていた。
主任研究員は、「<b>我々が恐れるべきは、このエッセンスそのものではない。エッセンスを操る、あるいはエッセンスに操られる人間の心なのだ</b>」と報告。適合者の確保と管理は、VEPの最優先事項となった。
</p>`,
/*3---------------------------------------------------------------------------*/
    `<p>VEPの内部では、ヴォイド・エッセンスに対する根本的な哲学の対立が深まっていた。
一つ目の勢力である「<b>制御派（The Containment）</b>」は、エッセンスを人類の存在基盤を脅かす毒と見なし、その完全な封印と無力化を主張した。
彼らにとって、エッセンスは「世界のバグであり、修正が必要」であった。
対照的に、二つ目の勢力「<b>活用派（The Utilization）</b>」は、エッセンスを人類文明を次なる段階へ進める「資源」であり「進化の鍵」と捉え、管理された利用を推進。
彼らは「この力を恐れるな、我々は新たな世界の神々になるのだ」と豪語した。
そして三つ目の勢力「<b>真実探求派（The Truth Seekers）</b>」は、利用や封印よりも、リフトとエッセンスの起源、
そして「少年が消えた真の理由」の解明を最優先とした。彼らはエッセンスを情報として捉え、「原因を知らずして結果を制御しようとするのは傲慢だ」と、他の二派を批判した。
この三つの思想の亀裂は深まるばかりで、組織の崩壊を予感させていた。
</p>`,
/*4---------------------------------------------------------------------------*/
    `<p><span class="void-glow">ロスト・コア事件と計画の破綻</span>
計画開始から約2年後、VEPの中核施設「ノード・センター」で大事件が起こった。
事件の核心は、最も純粋で強力なエッセンスの塊「ロスト・コア（The Lost Core）」の忽然とした消失である。
この消失は、コアを用いて人体実験を強行しようとした活用派と、それに強く反対していた制御派**の対立が激化する中で発生した。
ロスト・コアが<b>内部の誰かに盗まれた</b>という疑念がVEPを崩壊させた。
制御派： 政府管理の限界を悟り離反。独自の封印技術と適合者を連れて、エッセンスの悪用を阻止する使命を帯びる。
活用派： 盗難疑惑で立場を失い、残存技術を手に潜伏。企業や地下組織と結託し、独自の超兵器開発を画策する。
真実探求派： コアが「自ら消失した可能性」も視野に入れ、事件の背後にあるリフトの真実解明に動く。
この事件により、国際的な協力体制は完全に崩壊。エッセンスを巡る争いは、「元仲間同士の思想戦争」へと変質した。
</p>`,
/*5---------------------------------------------------------------------------*/
    `<p>VEPがロスト・コア事件で崩壊し、適合者が離散した後の世界。リフトが閉じたことで平穏が戻ったかに見えたが、
エッセンスはすでに空気、水、そして適合者の内側に残っていた。
この内側のエッセンスと精神が融合した適合者たちは、新たな異能形態「<span class="void-glow">ヴォイド・アバター</span>」へと変貌した。
アバターの能力は、その人物の「<b>虚無（Void）</b>」、すなわち深き後悔や揺るぎない信念などを具現化した力である。人々は、この超常的な力を持つアバターたちに、恐れと興味を抱いた。
アバターの発現と同時に、彼らのいる場所の時空が歪み、「<span class="void-glow">共鳴場（レゾナンス・フィールド）</span>」が出現し始めた。
これはアバター同士の力が引き合い、エッセンスが空間を書き換える特殊な戦場である。共鳴場の中では現実の法則が歪められ、人々は各地で展開される彼らの戦いを、ただ見守るしかなかった。
</p>`

  ];

  let index = 0;
  let firstClick = true;


  nextBtn.addEventListener("click", () => {

    // ✅ 初回クリックでタイトルLayerを非表示
    if (firstClick) {
      if (textLayer) textLayer.style.display = "none";
      storyContainer.classList.remove("hidden");
      storyContainer.classList.add("show");
      firstClick = false;
    }

    // ✅ 画像とテキストを今の index で表示
    storyImage.classList.remove("fade-in");
    storyText.classList.remove("fade-in");

    setTimeout(() => {
      storyImage.src = images[index];
      storyText.innerHTML = texts[index];
      storyImage.classList.add("fade-in");
      storyText.classList.add("fade-in");
    }, 120);
// ✅ ここに追加！（戻るボタンを表示開始）
if (index > 0) {
  prevBtn.style.display = "inline-block";
}
    // ✅ ボタン文言切り替え（ index が最後の時だけ変える ）
    if (index === images.length - 1) {
      nextBtn.textContent = "最初に戻る";
    } else {
      nextBtn.textContent = "次へ";
    }

    // ✅ 表示後にインデックスを増やす
    index++;

    // ✅ 最後の次でリセット（ループ）
    if (index >= images.length) {
      index = 0;
    }
  });
   // ✅ 戻るボタン（prevBtn）クリック処理
prevBtn.addEventListener("click", () => {
  index--; // ひとつ前へ戻る
  if (index < 0) {
    index = images.length - 1; // 先頭で戻る → 最後へループ
  }

  storyImage.classList.remove("fade-in");
  storyText.classList.remove("fade-in");

  setTimeout(() => {
    storyImage.src = images[index];
    storyText.innerHTML = texts[index];
    storyImage.classList.add("fade-in");
    storyText.classList.add("fade-in");
  }, 120);
  

  // ✅ ボタン制御
  prevBtn.style.display = (index === 0) ? "none" : "inline-block";
  nextBtn.textContent = "次へ"; // ← 戻るときは「最初に戻る」を解除して通常に
});

const scrollTopBtn = document.getElementById("scrollTopBtn");

scrollTopBtn.addEventListener("click", () => {
  window.scrollTo({ top: 0, behavior: "smooth" });
});


});

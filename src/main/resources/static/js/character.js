// --- キャラクターデータ ---
function createChar(name, img, thumb, origin='', pos='', story='データなし', skill='', ability='') {
    return {name,img,thumb,origin,pos,story,skill,ability};
}

// --- キャラクターデータ ---
function createChar(name, img, thumb, origin='', pos='', story='データなし', skill='', ability='') {
    return {name,img,thumb,origin,pos,story,skill,ability};
}

const characters = {
    'abdul': createChar('アブドゥル・ハッサン','/images/char/abdul_main.jpeg','/images/char/abdul_banner.jpg','エジプト','サポート',
        '砂漠の古代遺跡で、境界の不安定化と同時に発見された「秘術」を使う探掘家。奇妙な光のトーテムや砂を操る装置で戦場を撹乱する。彼の目的は、境界技術を解明することにある。',
        '【沈黙の砂時計】指定地点に砂の渦を発生させるトーテムを設置。範囲内の敵はスキルを使用できなくなる（沈黙効果）が、トーテムは敵に破壊される可能性がある。',
        '【熱波の幻影】敵の視界外から攻撃する際、ダメージが上昇し、敵が彼をターゲットするまで時間がかかる。'
    ),
    'wolf': createChar('ヴォルフ・ヴァイスマン','/images/char/volf_main.jpeg','/images/char/volf_banner.jpg','ドイツ','タンク',
        '元特殊部隊のエリート。厳格な規律と徹底した防御戦術を重んじる。異世界からの侵入に対し、「人類の防壁」として巨大な電磁シールドを装備し、最前線を死守する。',
        '【アイゼン・シールド】自身の正面に強力なエネルギーシールドを生成。シールド展開中は移動できないが、正面からの被ダメージを激減させ、シールドに触れた敵に微弱な電撃ダメージを与える。',
        '【戦術指揮】彼の周囲にいる味方には、わずかなダメージカット効果と被ノックバック耐性が付与される。'
    ),
    'sota': createChar('界守 颯太','/images/char/souta_main.jpeg','/images/char/souta_banner.jpg','日本','アタッカー（近接）',
        '日本の秘密組織「境界守」のエース。異世界の影響を「穢れ」として一刀両断する。東洋の剣術と独自の技術を組み合わせた超高速戦闘を得意とする。',
        '【無双・剣閃】敵一体に高速で突進し、一瞬で複数回斬りつける。攻撃後、一時的に敵の防御を無視するバフを得る。',
        '【気配遮断】非戦闘時の移動速度が速く、戦闘から離脱後、短時間でHPが自然回復し始める。'
    ),
    'gaby': createChar('ガブリエル・オルテガ','/images/char/gaburiel_main.jpg','/images/char/gaburiel_banner.jpg','ブラジル','アタッカー（近接）',
        'スラム街出身のストリートファイター。境界の混乱に乗じて力を手に入れ、その超人的な身体能力を駆使して戦う。戦場を動き回り、接近戦で敵を圧倒することに快感を覚える。',
        '【マチェーテ・チャージ】巨大なマチェーテ（山刀）を構え、突進。最初に当たった敵を地面に叩きつけ（ノックダウン）、短時間行動不能にする。',
        '【アドレナリン・ラッシュ】敵を倒すたびに、一定時間攻撃力と移動速度が大きく上昇する。'
    ),
    'zeroone': createChar('ゼロ・ワン','/images/char/zero_one_main.jpeg','/images/char/zero_one_banner.jpg','出自不明','アタッカー（近接）',
        '異世界技術で作られた戦闘用人造人間（ドール）。感情を持たず、与えられた「境界の破壊」という命令を忠実に実行する。変形自在なボディを持ち、予測不能な動きで敵を翻弄する。',
        '【質量転換：ブレード】全身を変形させて鋭いブレードを生成。ターゲットに高速で突撃し、攻撃を避けながら多段ヒットさせる。',
        '【予測エラー】敵から受ける初回ダメージを一定量カットし、直後の移動速度が大きく上昇する。'
    ),
    'echo': createChar('エコー','/images/char/echo_main.jpeg','/images/char/echo_banner.jpg','北米の古い森','アタッカー（中・遠距離）',
        '北米の古い森に宿る、自然の精霊。境界の不安定化を「汚染」とみなし、自然の力を借りて戦う。透明な体と、大地のエネルギーを操る能力で、戦場に予測不能な自然現象を発生させる。',
        '【大地の囁き】敵の足元から巨大なツタを急速に生み出し、範囲内の敵全てを短時間行動不能（ルート）にする。',
        '【環境支配】彼女が攻撃する場所の環境（地面など）を「汚染」状態にし、そのエリアにいる敵の移動速度を低下させる。'
    ),
    'kai': createChar('カイ・スパーク・リチャーズ','/images/char/kai_main.jpeg','/images/char/kai_banner.jpg','イギリス','近接',
        '元電気技師だが、境界のエネルギーに偶然接触したことで、自身の体で電気を生成・操作する能力を得た。その力で敵を麻痺させ、自身は素早い動きで戦場を駆け回る。彼の目的は、自身の能力の源を探ることにある。',
        '【電撃スタナー】前方に電気の球を投げつけ、命中した敵にダメージを与え、短時間行動を麻痺させる（スタン）。このスキルは近距離でより効果的。',
        '【過充電】敵に近接攻撃を当てるたびに、自身の移動速度が短時間上昇し、次の電撃スキルのクールダウンが短縮される。'
    ),
    'lia': createChar('リア・チャン','/images/char/ria_main.jpeg','/images/char/ria_banner.jpg','中国','サポーター',
        '古代の気功術を現代に伝える武術家。自身の「気」を光のエネルギーに変え、味方の治癒と強化に用いる。その穏やかな笑顔とは裏腹に、チームを守るための意志は鋼のように強い。',
        '【生命の気功陣】足元に巨大な円陣を展開。範囲内の味方のHPを持続的に高速回復させ、同時に攻撃力を上昇させる（バフ）。',
        '【気の流れ】味方を回復するたびに、彼女自身のスキルクールダウンがわずかに短縮される。'
    ),
    'picco': createChar('ピコ','/images/char/pico_main.jpeg','/images/char/pico_banner.jpg','境界の狭間（異世界）','サポーター',
        '境界の不安定なエネルギーから生まれた、小さなふわふわとした精霊。好奇心旺盛で、人間や異世界の存在に興味津々。戦闘能力はほとんどないが、無邪気な力で味方の傷を癒し、時には敵を困惑させる。',
        '【癒しの粉】自身の周囲に輝く粉をまき散らし、範囲内の味方のHPを継続的に回復させる。この粉は敵の視界をわずかに妨害する効果もある。',
        '【陽気なオーラ】ピコが近くにいる味方には、わずかな移動速度上昇効果と、状態異常からの回復速度上昇効果が付与される。'
    ),
    'raj': createChar('ラジャ・サルカール','/images/char/raja_main.jpg','/images/char/raja_banner.jpg','インド','アタッカー（遠距離）',
        'ヒマラヤの隠れた村に住む、炎の秘術を操る長老。境界の不安定化が世界のバランスを崩していることを察し、その炎で「浄化」しようと戦場に現れた。彼の炎はただ燃やすだけでなく、空間を操り、味方を守ることもできる。',
        '【燃え盛る障壁】指定した場所に巨大な炎の壁を生成する。炎の壁は敵の弾丸をブロックし、触れた敵に継続的なダメージを与える。味方は炎の壁を透過できる。',
        '【業火の慈悲】敵に炎ダメージを与えるたびに、自身とその周囲の味方のHPがわずかに回復する。'
    ),
    'fudou': createChar('フドウ','/images/char/fudou_main.jpeg','/images/char/fudou_banner.jpg','極寒の荒野（異世界）','アタッカー（近接）',
        '極寒の荒野を生き抜いてきたオオカミの獣人。群れの縄張りが境界の拡大により脅かされ、その原因を探るため、そして新たな狩場を求めて戦いに身を投じた。嗅覚と聴覚が非常に発達しており、敵の位置を正確に察知する。',
        '【ワイルドハント】前方に素早く突進し、当たった敵に大ダメージを与える。この突進中、敵の設置型トラップや壁を透過できる。',
        '【野生の嗅覚】近くにいるHPが減少している敵の位置を、壁越しでも視覚化して感知できる。また、敵が残した足跡や痕跡を追跡する能力に長けている。'
    ),
    'nightmare': createChar('ナイトメア','/images/char/nightmare_main.jpeg','/images/char/nightmare_banner.jpg','逢魔時（異世界）','アタッカー（中・近接）',
        '異世界「逢魔時」の深淵から現れた、感情や実体を持たない影のような存在。敵の恐怖心を餌とし、姿を隠して敵を闇へと引きずり込む。境界の拡大を自身の「縄張り」の拡張と捉えている。',
        '【闇の抱擁】自身が透明化し、周囲の敵から完全に視認されなくなる。透明化中は攻撃できないが、次の通常攻撃がクリティカルとなり、敵を短時間「恐慌」状態にする（移動速度低下と攻撃力減少）。',
        '【恐怖の残響】敵が倒されるたびに、近くにいる敵の移動速度がわずかに低下し、自身は短時間、攻撃力が上昇する。'
    ),
    'valner': createChar('ヴァルナー','/images/char/valner_main.jpg','/images/char/valner_banner.jpg','宇宙の深淵（異世界）','アタッカー（近接）',
        '異世界から境界を越えて侵入した、知性を持つ黒い粘液状の生命体。現在の肉体（宿主）は、境界エリアで発見した強化スーツの残骸と一体化している。その目的は、より強力で安定した「宿主」を見つけ、この次元を完全に支配すること。',
        '【漆黒の触手】身体の一部を黒い粘液状の触手に変形させ、指定した敵に投げつける。命中した敵を短時間拘束し、引き寄せる。敵のスキル使用を中断させる効果もある。',
        '【肉体変質】ヴァルナーは常に微細な自己修復を行っており、非戦闘時だけでなく、敵を拘束している間もわずかにHPが持続回復する。'
    ),
    'celine': createChar('セリーヌ・デュボア','/images/char/celine_main.jpeg','/images/char/celine_banner.jpg','フランス','アタッカー（遠距離）',
        '知的な貴族の令嬢。境界の力の源を「未知の芸術」として研究し、自作の「魔導ライフル」でその力を制御する。冷静沈着で、常に戦場の最も安全な場所から敵を精密に撃ち抜く。',
        '【エレガント・ショット】数秒間チャージした後、視界内の最も遠い敵を狙撃。敵のHPが低いほどクリティカルダメージが上昇する。',
        '【完璧な位置】5秒以上移動せずにいると、射程距離と攻撃力がわずかに増加する。'
    )
};


// --- DOM取得 ---
const charBackgroundEl = document.getElementById('charBackground');
const charNameEl = document.getElementById('charName');
const charOriginEl = document.getElementById('charOrigin');
const charPositionEl = document.getElementById('charPosition');
const charStoryEl = document.getElementById('charStory');
const charSkillEl = document.getElementById('charSkill');
const charAbilityEl = document.getElementById('charAbility');
const sidebarEl = document.getElementById('sidebar-buttons');

// --- サイドボタン生成 ---
Object.keys(characters).forEach(key => {
    const char = characters[key];
    const btn = document.createElement('div');
    btn.classList.add('char-select-btn');
    btn.innerHTML = `<img class="char-thumbnail" src="${char.thumb}" alt="${char.name}">
                     <div class="char-label">${char.name}</div>`;
    btn.addEventListener('click', () => selectChar(key));
    sidebarEl.appendChild(btn);
});

// --- キャラクター選択 ---
function selectChar(key){
    const char = characters[key];
    charBackgroundEl.src = char.img;
    charNameEl.textContent = char.name;
    charOriginEl.textContent = char.origin;
    charPositionEl.textContent = char.pos;
    charStoryEl.textContent = char.story;
    charSkillEl.textContent = char.skill;
    charAbilityEl.textContent = char.ability;

    document.querySelectorAll('.char-select-btn').forEach(btn => btn.classList.remove('active'));
    const activeBtn = Array.from(sidebarEl.children).find(btn => btn.textContent === char.name);
    if(activeBtn) activeBtn.classList.add('active');
}

// 初期選択
selectChar(Object.keys(characters)[0]);




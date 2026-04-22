const API = '';
let editingProjectId = null;
let techTags = [], featTags = [];
let devPointItems = [], troubleItems = [];

/* ── Auth ── */
async function doLogin() {
  const username = document.getElementById('login-username').value.trim();
  const password = document.getElementById('login-password').value;
  const errEl    = document.getElementById('login-error');
  const btn      = document.getElementById('login-btn');

  if (!username || !password) { errEl.textContent = '아이디와 비밀번호를 입력하세요.'; return; }
  btn.disabled = true; btn.textContent = '로그인 중...';
  errEl.textContent = '';

  try {
    const res = await adminFetch(API + '/api/admin/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify({ username, password }),
    });
    if (!res.ok) { errEl.textContent = '아이디 또는 비밀번호가 올바르지 않습니다.'; return; }
    document.getElementById('topbar-user').textContent = username;
    showDashboard();
  } catch {
    errEl.textContent = '서버에 연결할 수 없습니다.';
  } finally {
    btn.disabled = false; btn.textContent = '로그인';
  }
}

async function doLogout() {
  await adminFetch(API + '/api/admin/logout', { method: 'POST', credentials: 'include' });
  document.getElementById('dashboard').style.display = 'none';
  document.getElementById('login-screen').style.display = 'flex';
  document.getElementById('login-password').value = '';
}

function authHeader() {
  return { 'Content-Type': 'application/json' };
}

function adminFetch(url, options = {}) {
  return fetch(url, { ...options, credentials: 'include' });
}

async function showDashboard() {
  document.getElementById('login-screen').style.display = 'none';
  document.getElementById('dashboard').style.display = 'block';
  loadProjects(0);
  loadProfile();
}

/* ── Tab ── */
function switchTab(tab) {
  document.querySelectorAll('.tab-panel').forEach(el => el.classList.remove('active'));
  document.querySelectorAll('.sidebar-item').forEach(el => el.classList.remove('active'));
  document.getElementById('tab-' + tab).classList.add('active');
  document.querySelector(`.sidebar-item[data-tab="${tab}"]`).classList.add('active');
  if (tab === 'contacts')     loadContacts(0);
  if (tab === 'profile')      loadProfile();
  if (tab === 'projects')     { showProjectList(); }
  if (tab === 'skills')       loadSkills();
  if (tab === 'experience')   { showExpList(); }
  if (tab === 'education')    loadEducation();
  if (tab === 'certification') loadCertification();
}

/* ── Toast ── */
let toastTimer;
function toast(msg, type = 'success') {
  const el = document.getElementById('toast');
  el.textContent = msg;
  el.className = 'show ' + type;
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => { el.className = ''; }, 2800);
}

/* ─────────────────────────────────────────────
   PROJECTS
───────────────────────────────────────────── */
let projectsPage = 0;
let projectsTotalPages = 1;
let contentBlocks = [];

async function loadProjects(page = 0) {
  projectsPage = page;
  try {
    const res = await fetch(`${API}/api/projects?page=${page}&size=10`);
    const data = await res.json();
    const tbody = document.getElementById('projects-tbody');
    tbody.innerHTML = '';

    if (!data.content || data.content.length === 0) {
      tbody.innerHTML = '<tr><td colspan="7" class="empty">등록된 프로젝트가 없습니다.</td></tr>';
      renderPagination('projects', 0, 0);
      return;
    }

    data.content.forEach(p => {
      const techStr = (p.techStack || []).slice(0, 3).join(', ') + (p.techStack?.length > 3 ? '...' : '');
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${p.sortOrder ?? '-'}</td>
        <td><strong>${esc(p.title)}</strong></td>
        <td><span style="color:var(--muted);font-size:0.8rem">${esc(techStr)}</span></td>
        <td>${p.githubUrl ? `<a href="${esc(p.githubUrl)}" target="_blank" style="color:var(--accent);font-size:0.8rem">↗</a>` : '-'}</td>
        <td>${p.demoUrl ? `<a href="${esc(p.demoUrl)}" target="_blank" style="color:var(--cyan);font-size:0.8rem">↗</a>` : '-'}</td>
        <td style="color:var(--muted);font-size:0.8rem">${p.createdAt || '-'}</td>
        <td>
          <div class="td-actions">
            <button class="btn btn-edit btn-sm" onclick="editProject(${p.id})">수정</button>
            <button class="btn btn-danger btn-sm" onclick="deleteProject(${p.id}, '${esc(p.title)}')">삭제</button>
          </div>
        </td>
      `;
      tbody.appendChild(tr);
    });

    projectsTotalPages = data.totalPages || 1;
    renderPagination('projects', page, data.totalPages);
  } catch {
    toast('프로젝트 목록을 불러오지 못했습니다.', 'error');
  }
}

async function editProject(id) {
  try {
    const res = await fetch(`${API}/api/projects/${id}`);
    const p = await res.json();
    showProjectEditor(id, p);
  } catch {
    toast('프로젝트 정보를 불러오지 못했습니다.', 'error');
  }
}

async function deleteProject(id, title) {
  if (!confirm(`"${title}" 프로젝트를 삭제할까요?`)) return;
  try {
    const res = await adminFetch(`${API}/api/admin/projects/${id}`, {
      method: 'DELETE', headers: authHeader()
    });
    if (res.status === 204 || res.ok) {
      toast('삭제되었습니다.');
      loadProjects(projectsPage);
    } else if (res.status === 401) {
      toast('인증이 만료되었습니다.', 'error'); doLogout();
    } else {
      toast('삭제에 실패했습니다.', 'error');
    }
  } catch {
    toast('요청 중 오류가 발생했습니다.', 'error');
  }
}

async function saveProject() {
  const title = document.getElementById('proj-title').value.trim();
  const desc  = document.getElementById('proj-desc').value.trim();
  if (!title || !desc) { toast('제목과 설명은 필수입니다.', 'error'); return; }

  collectPointItems();
  collectContentBlocks();
  const body = {
    title,
    summary:        document.getElementById('proj-summary').value.trim() || null,
    description:    desc || null,
    githubUrl:      document.getElementById('proj-github').value.trim() || null,
    demoUrl:        document.getElementById('proj-demo').value.trim() || null,
    duration:       document.getElementById('proj-duration').value.trim() || null,
    teamSize:       document.getElementById('proj-team').value.trim() || null,
    role:           document.getElementById('proj-role').value.trim() || null,
    sortOrder:      parseInt(document.getElementById('proj-order').value) || 0,
    techStack:      techTags,
    features:       featTags,
    devPoints:      devPointItems.filter(d => d.label && d.content),
    troubleshooting: troubleItems.filter(t => t.label && t.content),
    contentBlocks:  contentBlocks.filter(b => b.content),
  };

  try {
    const url    = editingProjectId ? `${API}/api/admin/projects/${editingProjectId}` : `${API}/api/admin/projects`;
    const method = editingProjectId ? 'PUT' : 'POST';
    const res    = await adminFetch(url, { method, headers: authHeader(), body: JSON.stringify(body) });

    if (res.ok) {
      toast(editingProjectId ? '수정되었습니다.' : '등록되었습니다.');
      showProjectList();
    } else if (res.status === 401) {
      toast('인증이 만료되었습니다.', 'error'); doLogout();
    } else {
      const err = await res.json().catch(() => ({}));
      toast(err.message || '저장에 실패했습니다.', 'error');
    }
  } catch {
    toast('요청 중 오류가 발생했습니다.', 'error');
  }
}

function showProjectEditor(id, data) {
  editingProjectId = id || null;

  document.getElementById('proj-title').value    = data?.title || '';
  document.getElementById('proj-summary').value  = data?.summary || '';
  document.getElementById('proj-desc').value     = data?.description || '';
  document.getElementById('proj-github').value   = data?.githubUrl || '';
  document.getElementById('proj-demo').value     = data?.demoUrl || '';
  document.getElementById('proj-duration').value = data?.duration || '';
  document.getElementById('proj-team').value     = data?.teamSize || '';
  document.getElementById('proj-role').value     = data?.role || '';
  document.getElementById('proj-order').value    = data?.sortOrder ?? '';

  techTags = [...(data?.techStack || [])];
  featTags = [...(data?.features || [])];
  devPointItems = (data?.devPoints || []).map(d => ({ label: d.label, content: d.content, imageUrl: d.imageUrl || '' }));
  troubleItems  = (data?.troubleshooting || []).map(t => ({ label: t.label, content: t.content, imageUrl: t.imageUrl || '' }));
  contentBlocks = (data?.contentBlocks || []).map(b => ({ blockType: b.blockType, content: b.content }));

  renderTagChips('tech');
  renderTagChips('feat');
  renderPointItems('devpoint');
  renderPointItems('trouble');
  renderContentBlocks();

  const mediaSection = document.getElementById('proj-media-section');
  if (id) {
    mediaSection.style.display = 'block';
    renderMediaList(data?.media || []);
  } else {
    mediaSection.style.display = 'none';
    document.getElementById('proj-media-list').innerHTML = '';
  }

  document.getElementById('project-list-view').style.display = 'none';
  document.getElementById('project-edit-view').style.display = 'block';
}

function showProjectList() {
  document.getElementById('project-edit-view').style.display = 'none';
  document.getElementById('project-list-view').style.display = 'block';
  loadProjects(projectsPage);
}

/* ─────────────────────────────────────────────
   CONTACTS
───────────────────────────────────────────── */
let contactsPage = 0;

async function loadContacts(page = 0) {
  contactsPage = page;
  const unreadOnly = document.getElementById('unread-only')?.checked ?? false;
  try {
    const res  = await adminFetch(`${API}/api/admin/contacts?page=${page}&size=15&unreadOnly=${unreadOnly}`, {
      headers: authHeader()
    });
    if (res.status === 401) { toast('인증이 만료되었습니다.', 'error'); doLogout(); return; }
    const data = await res.json();
    const tbody = document.getElementById('contacts-tbody');
    tbody.innerHTML = '';

    if (!data.content || data.content.length === 0) {
      tbody.innerHTML = '<tr><td colspan="6" class="empty">문의가 없습니다.</td></tr>';
      renderPagination('contacts', 0, 0);
      return;
    }

    data.content.forEach(c => {
      const dateStr = c.createdAt ? c.createdAt.replace('T', ' ').slice(0, 16) : '-';
      const tr = document.createElement('tr');
      tr.id = `contact-row-${c.id}`;
      tr.innerHTML = `
        <td><span class="badge ${c.isRead ? 'badge-read' : 'badge-unread'}" id="badge-${c.id}">${c.isRead ? '읽음' : '미읽음'}</span></td>
        <td>${esc(c.name)}</td>
        <td style="font-size:0.8rem;color:var(--muted)">${esc(c.email)}</td>
        <td class="msg-cell">
          <div class="msg-short" id="msg-short-${c.id}">${esc(c.message)}</div>
          <div class="msg-full"  id="msg-full-${c.id}">${esc(c.message)}</div>
          <span class="msg-toggle" onclick="toggleMsg(${c.id})" id="msg-toggle-${c.id}">더 보기</span>
        </td>
        <td style="color:var(--muted);font-size:0.8rem;white-space:nowrap">${dateStr}</td>
        <td>
          ${!c.isRead ? `<button class="btn btn-success btn-sm" id="read-btn-${c.id}" onclick="markRead(${c.id})">읽음 처리</button>` : ''}
        </td>
      `;
      tbody.appendChild(tr);
    });

    renderPagination('contacts', page, data.totalPages);
  } catch {
    toast('문의 목록을 불러오지 못했습니다.', 'error');
  }
}

function toggleMsg(id) {
  const s = document.getElementById('msg-short-' + id);
  const f = document.getElementById('msg-full-'  + id);
  const t = document.getElementById('msg-toggle-' + id);
  const isExpanded = f.style.display === 'block';
  s.style.display = isExpanded ? '' : 'none';
  f.style.display = isExpanded ? 'none' : 'block';
  t.textContent   = isExpanded ? '더 보기' : '접기';
}

async function markRead(id) {
  try {
    const res = await adminFetch(`${API}/api/admin/contacts/${id}/read`, {
      method: 'PATCH', headers: authHeader()
    });
    if (res.status === 401) { toast('인증이 만료되었습니다.', 'error'); doLogout(); return; }
    if (res.ok) {
      const badge = document.getElementById('badge-' + id);
      if (badge) { badge.textContent = '읽음'; badge.className = 'badge badge-read'; }
      const btn = document.getElementById('read-btn-' + id);
      if (btn) btn.remove();
      toast('읽음 처리되었습니다.');
    }
  } catch {
    toast('요청 중 오류가 발생했습니다.', 'error');
  }
}

/* ─────────────────────────────────────────────
   PROFILE
───────────────────────────────────────────── */
async function loadProfile() {
  try {
    const res  = await fetch(API + '/api/profile');
    const data = await res.json();
    document.getElementById('pf-name').value   = data.name         || '';
    document.getElementById('pf-role').value   = data.role         || '';
    document.getElementById('pf-email').value  = data.email        || '';
    document.getElementById('pf-github').value = data.githubUrl    || '';
    document.getElementById('pf-exp').value    = data.expYears     ?? '';
    document.getElementById('pf-count').value  = data.projectCount ?? '';
    document.getElementById('pf-bio').value    = data.bio          || '';
  } catch {
    toast('프로필 정보를 불러오지 못했습니다.', 'error');
  }
}

async function saveProfile() {
  const body = {
    name:         document.getElementById('pf-name').value.trim(),
    role:         document.getElementById('pf-role').value.trim(),
    email:        document.getElementById('pf-email').value.trim(),
    githubUrl:    document.getElementById('pf-github').value.trim() || null,
    bio:          document.getElementById('pf-bio').value.trim(),
    expYears:     parseInt(document.getElementById('pf-exp').value)   || 0,
    projectCount: parseInt(document.getElementById('pf-count').value) || 0,
  };
  try {
    const res = await adminFetch(API + '/api/admin/profile', {
      method: 'PUT', headers: authHeader(), body: JSON.stringify(body)
    });
    if (res.status === 401) { toast('인증이 만료되었습니다.', 'error'); doLogout(); return; }
    if (res.ok) {
      toast('프로필이 저장되었습니다.');
    } else {
      const err = await res.json().catch(() => ({}));
      toast(err.message || '저장에 실패했습니다.', 'error');
    }
  } catch {
    toast('요청 중 오류가 발생했습니다.', 'error');
  }
}

/* ─────────────────────────────────────────────
   SKILLS
───────────────────────────────────────────── */
let editingCategoryId = null;
let targetCategoryId  = null;

async function loadSkills() {
  try {
    const data = await adminFetch(`${API}/api/admin/skills`, { headers: authHeader() }).then(r => r.json());
    const wrap = document.getElementById('skills-categories');
    if (!data.length) { wrap.innerHTML = '<p class="empty">등록된 카테고리가 없습니다.</p>'; return; }
    const levelLabel = ['','입문','경험','익숙','능숙'];
    wrap.innerHTML = data.map(cat => `
      <div style="margin-bottom:1.5rem;border:1px solid var(--border);border-radius:0.8rem;overflow:hidden;background:var(--surface)">
        <div style="display:flex;align-items:center;justify-content:space-between;padding:0.75rem 1rem;background:var(--surface2)">
          <strong style="font-size:0.95rem">${esc(cat.name)}</strong>
          <div style="display:flex;gap:0.5rem">
            <button class="btn btn-edit btn-sm" onclick="openSkillModal(${cat.id})">+ 스킬</button>
            <button class="btn btn-edit btn-sm" onclick="openCategoryModal(${cat.id},'${esc(cat.name)}',${cat.sortOrder})">수정</button>
            <button class="btn btn-danger btn-sm" onclick="deleteCategory(${cat.id},'${esc(cat.name)}')">삭제</button>
          </div>
        </div>
        <div style="display:flex;flex-wrap:wrap;gap:0.5rem;padding:0.75rem 1rem">
          ${cat.skills.length ? cat.skills.map(s => `
            <span style="display:inline-flex;align-items:center;gap:0.4rem;background:rgba(37,99,235,0.05);border:1px solid var(--border);border-radius:2rem;padding:0.25rem 0.7rem;font-size:0.82rem">
              ${esc(s.name)}
              <span style="font-size:0.7rem;color:var(--muted)">${levelLabel[s.level] || 'Lv' + s.level}</span>
              <button style="background:none;border:none;cursor:pointer;color:var(--muted);font-size:0.7rem" onclick="deleteSkill(${s.id},'${esc(s.name)}')">✕</button>
            </span>`).join('') : '<span style="color:var(--muted);font-size:0.82rem">스킬 없음</span>'}
        </div>
      </div>`).join('');
  } catch { toast('스킬 목록을 불러오지 못했습니다.', 'error'); }
}

/* ── Media ── */
function renderMediaList(mediaItems) {
  const container = document.getElementById('proj-media-list');
  container.innerHTML = mediaItems.map(m => `
    <div style="position:relative;width:120px;">
      ${m.mediaType === 'VIDEO'
        ? `<video src="${m.url}" style="width:120px;height:80px;object-fit:cover;border-radius:6px;background:#0f172a;" preload="metadata"></video>`
        : `<img src="${m.url}" style="width:120px;height:80px;object-fit:cover;border-radius:6px;" />`
      }
      <button onclick="deleteMedia(${m.id}, this.parentElement)"
        style="position:absolute;top:2px;right:2px;background:rgba(0,0,0,0.7);color:#fff;border:none;border-radius:50%;width:20px;height:20px;cursor:pointer;font-size:11px;line-height:1;">✕</button>
    </div>
  `).join('');
}

async function uploadMedia(event) {
  if (!editingProjectId) return;
  const files = Array.from(event.target.files);
  for (const file of files) {
    const formData = new FormData();
    formData.append('file', file);
    try {
      const res = await adminFetch(`${API}/api/admin/projects/${editingProjectId}/media`, {
        method: 'POST', headers: authHeaderMultipart(), body: formData
      });
      if (res.ok) {
        const media = await res.json();
        const container = document.getElementById('proj-media-list');
        const div = document.createElement('div');
        div.style.cssText = 'position:relative;width:120px;';
        div.innerHTML = media.mediaType === 'VIDEO'
          ? `<video src="${media.url}" style="width:120px;height:80px;object-fit:cover;border-radius:6px;background:#0f172a;" preload="metadata"></video>`
          : `<img src="${media.url}" style="width:120px;height:80px;object-fit:cover;border-radius:6px;" />`;
        const btn = document.createElement('button');
        btn.textContent = '✕';
        btn.style.cssText = 'position:absolute;top:2px;right:2px;background:rgba(0,0,0,0.7);color:#fff;border:none;border-radius:50%;width:20px;height:20px;cursor:pointer;font-size:11px;line-height:1;';
        btn.onclick = () => deleteMedia(media.id, div);
        div.appendChild(btn);
        container.appendChild(div);
        toast('업로드 완료');
      } else {
        toast('업로드 실패', 'error');
      }
    } catch {
      toast('업로드 중 오류 발생', 'error');
    }
  }
  event.target.value = '';
}

async function deleteMedia(id, el) {
  if (!confirm('미디어를 삭제할까요?')) return;
  try {
    const res = await adminFetch(`${API}/api/admin/media/${id}`, {
      method: 'DELETE', headers: authHeader()
    });
    if (res.ok) { el.remove(); toast('삭제되었습니다.'); }
    else toast('삭제 실패', 'error');
  } catch {
    toast('요청 중 오류 발생', 'error');
  }
}

function authHeaderMultipart() {
  return {};
}

function openCategoryModal(id = null, name = '', sortOrder = 0) {
  editingCategoryId = id;
  document.getElementById('category-modal-title').textContent = id ? '카테고리 수정' : '카테고리 추가';
  document.getElementById('cat-name').value  = name;
  document.getElementById('cat-order').value = sortOrder;
  document.getElementById('category-modal').classList.add('open');
}
function closeCategoryModal() { document.getElementById('category-modal').classList.remove('open'); }

async function saveCategory() {
  const name = document.getElementById('cat-name').value.trim();
  if (!name) { toast('카테고리명을 입력해주세요.', 'error'); return; }
  const body = { name, sortOrder: parseInt(document.getElementById('cat-order').value) || 0 };
  const url    = editingCategoryId ? `${API}/api/admin/skills/categories/${editingCategoryId}` : `${API}/api/admin/skills/categories`;
  const method = editingCategoryId ? 'PUT' : 'POST';
  try {
    const res = await fetch(url, { method, headers: authHeader(), body: JSON.stringify(body) });
    if (res.ok) { toast(editingCategoryId ? '수정되었습니다.' : '추가되었습니다.'); closeCategoryModal(); loadSkills(); }
    else if (res.status === 401) { toast('인증 만료', 'error'); doLogout(); }
    else toast('저장 실패', 'error');
  } catch { toast('오류 발생', 'error'); }
}

async function deleteCategory(id, name) {
  if (!confirm(`"${name}" 카테고리(및 하위 스킬)를 삭제할까요?`)) return;
  try {
    const res = await adminFetch(`${API}/api/admin/skills/categories/${id}`, { method: 'DELETE', headers: authHeader() });
    if (res.status === 204 || res.ok) { toast('삭제되었습니다.'); loadSkills(); }
    else if (res.status === 401) { toast('인증 만료', 'error'); doLogout(); }
    else toast('삭제 실패', 'error');
  } catch { toast('오류 발생', 'error'); }
}

function openSkillModal(categoryId) {
  targetCategoryId = categoryId;
  document.getElementById('skill-name').value  = '';
  document.getElementById('skill-level').value = '4';
  document.getElementById('skill-order').value = '';
  document.getElementById('skill-modal').classList.add('open');
}
function closeSkillModal() { document.getElementById('skill-modal').classList.remove('open'); }

async function saveSkill() {
  const name = document.getElementById('skill-name').value.trim();
  if (!name) { toast('스킬명을 입력해주세요.', 'error'); return; }
  const body = { name, level: parseInt(document.getElementById('skill-level').value) || 1, sortOrder: parseInt(document.getElementById('skill-order').value) || 0 };
  try {
    const res = await adminFetch(`${API}/api/admin/skills/categories/${targetCategoryId}/skills`,
      { method: 'POST', headers: authHeader(), body: JSON.stringify(body) });
    if (res.ok) { toast('스킬이 추가되었습니다.'); closeSkillModal(); loadSkills(); }
    else if (res.status === 401) { toast('인증 만료', 'error'); doLogout(); }
    else toast('추가 실패', 'error');
  } catch { toast('오류 발생', 'error'); }
}

async function deleteSkill(id, name) {
  if (!confirm(`"${name}" 스킬을 삭제할까요?`)) return;
  try {
    const res = await adminFetch(`${API}/api/admin/skills/${id}`, { method: 'DELETE', headers: authHeader() });
    if (res.status === 204 || res.ok) { toast('삭제되었습니다.'); loadSkills(); }
    else if (res.status === 401) { toast('인증 만료', 'error'); doLogout(); }
    else toast('삭제 실패', 'error');
  } catch { toast('오류 발생', 'error'); }
}

/* ─────────────────────────────────────────────
   EXPERIENCE
───────────────────────────────────────────── */
let editingExpId = null;

async function loadExperience() {
  try {
    const list = await fetch(`${API}/api/experiences`).then(r => r.json());
    const tbody = document.getElementById('exp-tbody');
    if (!list.length) { tbody.innerHTML = '<tr><td colspan="5" class="empty">등록된 경험이 없습니다.</td></tr>'; return; }
    tbody.innerHTML = list.map(e => `
      <tr>
        <td><strong>${esc(e.title)}</strong></td>
        <td style="color:var(--muted);font-size:0.82rem">${esc(e.summary || '')}</td>
        <td style="color:var(--muted);font-size:0.82rem">${(e.techStack||[]).map(t=>esc(t)).join(', ')}</td>
        <td>${e.sortOrder}</td>
        <td><div class="td-actions">
          <button class="btn btn-edit btn-sm" onclick="editExp(${e.id})">수정</button>
          <button class="btn btn-danger btn-sm" onclick="deleteExp(${e.id},'${esc(e.title)}')">삭제</button>
        </div></td>
      </tr>`).join('');
  } catch { toast('경험 목록을 불러오지 못했습니다.', 'error'); }
}

async function editExp(id) {
  try {
    const list = await fetch(`${API}/api/experiences`).then(r => r.json());
    const e = list.find(x => x.id === id);
    if (!e) return;
    showExpEditor(id, e);
  } catch {
    toast('역량 정보를 불러오지 못했습니다.', 'error');
  }
}

const EXP_MAX_BULLETS = 3;
const EXP_BULLET_MAXLEN = 130;

function renderBulletInput(value = '') {
  const v = esc(value);
  return `<div class="bullet-item">
    <input type="text" maxlength="${EXP_BULLET_MAXLEN}" value="${v}" placeholder="예) 중복 예약 문제 : 여러 사용자가 동시에 예약할 때..." />
    <button type="button" class="btn btn-danger btn-sm" onclick="removeBullet(this)">×</button>
  </div>`;
}

function setBullets(listId, items) {
  const el = document.getElementById(listId);
  const values = (items && items.length) ? items : [''];
  el.innerHTML = values.map(renderBulletInput).join('');
}

function addBullet(listId) {
  const el = document.getElementById(listId);
  if (el.querySelectorAll('.bullet-item').length >= EXP_MAX_BULLETS) {
    toast(`섹션당 최대 ${EXP_MAX_BULLETS}개까지 입력할 수 있습니다.`, 'error');
    return;
  }
  el.insertAdjacentHTML('beforeend', renderBulletInput(''));
}

function removeBullet(btn) {
  const item = btn.closest('.bullet-item');
  const list = item.parentElement;
  if (list.querySelectorAll('.bullet-item').length <= 1) {
    item.querySelector('input').value = '';
    return;
  }
  item.remove();
}

function collectBullets(listId) {
  return Array.from(document.getElementById(listId).querySelectorAll('input'))
    .map(i => i.value.trim()).filter(Boolean);
}

async function populateLinkedProjectSelect(selectedId) {
  const select = document.getElementById('exp-linked-project');
  try {
    const res = await fetch(`${API}/api/projects?page=0&size=100`).then(r => r.json());
    const opts = ['<option value="">— 연결 안 함 —</option>']
      .concat((res.content || []).map(p =>
        `<option value="${p.id}"${p.id === selectedId ? ' selected' : ''}>${esc(p.title)}</option>`));
    select.innerHTML = opts.join('');
  } catch {
    select.innerHTML = '<option value="">(프로젝트 목록 불러오기 실패)</option>';
  }
}

function showExpEditor(id, data) {
  editingExpId = id || null;

  document.getElementById('exp-title').value     = data?.title || '';
  setBullets('exp-situation-list', data?.situation);
  setBullets('exp-approach-list',  data?.approach);
  setBullets('exp-learned-list',   data?.learned);
  document.getElementById('exp-stack').value     = (data?.techStack || []).join(', ');
  document.getElementById('exp-image').value     = data?.imageUrl || '';
  document.getElementById('exp-order').value     = data?.sortOrder ?? '';
  populateLinkedProjectSelect(data?.linkedProjectId ?? null);

  const preview = document.getElementById('exp-image-preview');
  if (data?.imageUrl) {
    preview.innerHTML = `<img src="${esc(data.imageUrl)}" />`;
  } else {
    preview.innerHTML = '';
  }

  document.getElementById('exp-list-view').style.display = 'none';
  document.getElementById('exp-edit-view').style.display = 'block';
}

function showExpList() {
  document.getElementById('exp-edit-view').style.display = 'none';
  document.getElementById('exp-list-view').style.display = 'block';
  loadExperience();
}

async function uploadExpImage(event) {
  const file = event.target.files[0];
  if (!file) return;
  const formData = new FormData();
  formData.append('file', file);
  formData.append('path', 'experiences');
  try {
    const res = await adminFetch(`${API}/api/admin/upload`, {
      method: 'POST',
      headers: { 'Authorization': 'Bearer ' + localStorage.getItem('token') },
      body: formData,
    });
    if (res.ok) {
      const { url } = await res.json();
      document.getElementById('exp-image').value = url;
      document.getElementById('exp-image-preview').innerHTML = `<img src="${esc(url)}" />`;
      toast('이미지가 업로드되었습니다.');
    } else {
      toast('이미지 업로드 실패', 'error');
    }
  } catch { toast('업로드 중 오류 발생', 'error'); }
  event.target.value = '';
}

async function saveExp() {
  const title     = document.getElementById('exp-title').value.trim();
  const situation = collectBullets('exp-situation-list');
  const approach  = collectBullets('exp-approach-list');
  const learned   = collectBullets('exp-learned-list');
  if (!title) { toast('제목은 필수입니다.', 'error'); return; }
  if (!situation.length || !approach.length || !learned.length) {
    toast('상황·접근·배운 점은 각각 최소 1개 항목이 필요합니다.', 'error'); return;
  }
  const stackRaw = document.getElementById('exp-stack').value.trim();
  const techStack = stackRaw ? stackRaw.split(',').map(s => s.trim()).filter(Boolean) : [];
  const linkedRaw = document.getElementById('exp-linked-project').value;
  const body = {
    icon: null,
    title, summary: null,
    situation, approach, learned, techStack,
    imageUrl: document.getElementById('exp-image').value.trim() || null,
    sortOrder: parseInt(document.getElementById('exp-order').value) || 0,
    linkedProjectId: linkedRaw ? parseInt(linkedRaw) : null,
  };
  const url = editingExpId ? `${API}/api/admin/experiences/${editingExpId}` : `${API}/api/admin/experiences`;
  const method = editingExpId ? 'PUT' : 'POST';
  try {
    const res = await adminFetch(url, { method, headers: authHeader(), body: JSON.stringify(body) });
    if (res.ok) { toast(editingExpId ? '수정되었습니다.' : '추가되었습니다.'); showExpList(); }
    else if (res.status === 401) { toast('인증 만료', 'error'); doLogout(); }
    else { const err = await res.json().catch(()=>({})); toast(err.message||'저장 실패', 'error'); }
  } catch { toast('오류 발생', 'error'); }
}

async function deleteExp(id, name) {
  if (!confirm(`"${name}" 경험을 삭제할까요?`)) return;
  try {
    const res = await adminFetch(`${API}/api/admin/experiences/${id}`, { method: 'DELETE', headers: authHeader() });
    if (res.status === 204 || res.ok) { toast('삭제되었습니다.'); loadExperience(); }
    else if (res.status === 401) { toast('인증 만료', 'error'); doLogout(); }
    else toast('삭제 실패', 'error');
  } catch { toast('오류 발생', 'error'); }
}

/* ─────────────────────────────────────────────
   EDUCATION
───────────────────────────────────────────── */
let editingEduId = null;

async function loadEducation() {
  try {
    const list = await fetch(`${API}/api/educations`).then(r => r.json());
    const tbody = document.getElementById('edu-tbody');
    if (!list.length) { tbody.innerHTML = '<tr><td colspan="6" class="empty">등록된 학력이 없습니다.</td></tr>'; return; }
    tbody.innerHTML = list.map(e => `
      <tr>
        <td><strong>${esc(e.degree)}</strong></td>
        <td>${esc(e.institution)}</td>
        <td style="color:var(--muted)">${esc(e.major)}</td>
        <td style="color:var(--muted);font-size:0.82rem">${esc(e.startDate||'')} ${e.endDate ? '~ '+esc(e.endDate) : ''}</td>
        <td>${e.sortOrder}</td>
        <td><div class="td-actions">
          <button class="btn btn-edit btn-sm" onclick="openEduModal(${e.id})">수정</button>
          <button class="btn btn-danger btn-sm" onclick="deleteEdu(${e.id},'${esc(e.degree)}')">삭제</button>
        </div></td>
      </tr>`).join('');
  } catch { toast('학력 목록을 불러오지 못했습니다.', 'error'); }
}

function openEduModal(id = null) {
  editingEduId = id;
  document.getElementById('edu-modal-title').textContent = id ? '학력 수정' : '학력 추가';
  if (!id) {
    ['edu-institution','edu-degree','edu-major','edu-start','edu-end','edu-order'].forEach(i => document.getElementById(i).value = '');
    document.getElementById('edu-modal').classList.add('open'); return;
  }
  fetch(`${API}/api/educations`).then(r => r.json()).then(list => {
    const e = list.find(x => x.id === id);
    if (!e) return;
    document.getElementById('edu-institution').value = e.institution || '';
    document.getElementById('edu-degree').value      = e.degree      || '';
    document.getElementById('edu-major').value       = e.major       || '';
    document.getElementById('edu-start').value       = e.startDate   || '';
    document.getElementById('edu-end').value         = e.endDate     || '';
    document.getElementById('edu-order').value       = e.sortOrder   ?? '';
    document.getElementById('edu-modal').classList.add('open');
  });
}
function closeEduModal() { document.getElementById('edu-modal').classList.remove('open'); }

async function saveEdu() {
  const institution = document.getElementById('edu-institution').value.trim();
  const degree      = document.getElementById('edu-degree').value.trim();
  const major       = document.getElementById('edu-major').value.trim();
  if (!institution || !degree || !major) { toast('학교명, 학위, 전공은 필수입니다.', 'error'); return; }
  const body = { institution, degree, major,
    startDate: document.getElementById('edu-start').value.trim() || null,
    endDate:   document.getElementById('edu-end').value.trim()   || null,
    sortOrder: parseInt(document.getElementById('edu-order').value) || 0 };
  const url = editingEduId ? `${API}/api/admin/educations/${editingEduId}` : `${API}/api/admin/educations`;
  const method = editingEduId ? 'PUT' : 'POST';
  try {
    const res = await fetch(url, { method, headers: authHeader(), body: JSON.stringify(body) });
    if (res.ok) { toast(editingEduId ? '수정되었습니다.' : '추가되었습니다.'); closeEduModal(); loadEducation(); }
    else if (res.status === 401) { toast('인증 만료', 'error'); doLogout(); }
    else { const err = await res.json().catch(()=>({})); toast(err.message||'저장 실패', 'error'); }
  } catch { toast('오류 발생', 'error'); }
}

async function deleteEdu(id, name) {
  if (!confirm(`"${name}" 학력을 삭제할까요?`)) return;
  try {
    const res = await adminFetch(`${API}/api/admin/educations/${id}`, { method: 'DELETE', headers: authHeader() });
    if (res.status === 204 || res.ok) { toast('삭제되었습니다.'); loadEducation(); }
    else if (res.status === 401) { toast('인증 만료', 'error'); doLogout(); }
    else toast('삭제 실패', 'error');
  } catch { toast('오류 발생', 'error'); }
}

/* ─────────────────────────────────────────────
   CERTIFICATION
───────────────────────────────────────────── */
let editingCertId = null;

async function loadCertification() {
  try {
    const list = await fetch(`${API}/api/certifications`).then(r => r.json());
    const tbody = document.getElementById('cert-tbody');
    if (!list.length) { tbody.innerHTML = '<tr><td colspan="6" class="empty">등록된 자격증/수상이 없습니다.</td></tr>'; return; }
    tbody.innerHTML = list.map(c => `
      <tr>
        <td><span class="badge ${c.type==='CERT'?'badge-unread':'badge-read'}">${c.type==='CERT'?'자격증':'수상'}</span></td>
        <td><strong>${esc(c.name)}</strong></td>
        <td style="color:var(--muted)">${esc(c.issuer||'-')}</td>
        <td style="color:var(--muted);font-size:0.82rem">${esc(c.acquiredDate||'-')}</td>
        <td>${c.sortOrder}</td>
        <td><div class="td-actions">
          <button class="btn btn-edit btn-sm" onclick="openCertModal(${c.id})">수정</button>
          <button class="btn btn-danger btn-sm" onclick="deleteCert(${c.id},'${esc(c.name)}')">삭제</button>
        </div></td>
      </tr>`).join('');
  } catch { toast('자격증 목록을 불러오지 못했습니다.', 'error'); }
}

function openCertModal(id = null) {
  editingCertId = id;
  document.getElementById('cert-modal-title').textContent = id ? '수정' : '자격증 / 수상 추가';
  if (!id) {
    document.getElementById('cert-type').value   = 'CERT';
    document.getElementById('cert-name').value   = '';
    document.getElementById('cert-issuer').value = '';
    document.getElementById('cert-date').value   = '';
    document.getElementById('cert-order').value  = '';
    document.getElementById('cert-modal').classList.add('open'); return;
  }
  fetch(`${API}/api/certifications`).then(r => r.json()).then(list => {
    const c = list.find(x => x.id === id);
    if (!c) return;
    document.getElementById('cert-type').value   = c.type         || 'CERT';
    document.getElementById('cert-name').value   = c.name         || '';
    document.getElementById('cert-issuer').value = c.issuer        || '';
    document.getElementById('cert-date').value   = c.acquiredDate  || '';
    document.getElementById('cert-order').value  = c.sortOrder     ?? '';
    document.getElementById('cert-modal').classList.add('open');
  });
}
function closeCertModal() { document.getElementById('cert-modal').classList.remove('open'); }

async function saveCert() {
  const name = document.getElementById('cert-name').value.trim();
  if (!name) { toast('이름을 입력해주세요.', 'error'); return; }
  const body = {
    type:         document.getElementById('cert-type').value,
    name,
    issuer:       document.getElementById('cert-issuer').value.trim() || null,
    acquiredDate: document.getElementById('cert-date').value.trim()   || null,
    sortOrder:    parseInt(document.getElementById('cert-order').value) || 0,
  };
  const url    = editingCertId ? `${API}/api/admin/certifications/${editingCertId}` : `${API}/api/admin/certifications`;
  const method = editingCertId ? 'PUT' : 'POST';
  try {
    const res = await fetch(url, { method, headers: authHeader(), body: JSON.stringify(body) });
    if (res.ok) { toast(editingCertId ? '수정되었습니다.' : '추가되었습니다.'); closeCertModal(); loadCertification(); }
    else if (res.status === 401) { toast('인증 만료', 'error'); doLogout(); }
    else { const err = await res.json().catch(()=>({})); toast(err.message||'저장 실패', 'error'); }
  } catch { toast('오류 발생', 'error'); }
}

async function deleteCert(id, name) {
  if (!confirm(`"${name}"을 삭제할까요?`)) return;
  try {
    const res = await adminFetch(`${API}/api/admin/certifications/${id}`, { method: 'DELETE', headers: authHeader() });
    if (res.status === 204 || res.ok) { toast('삭제되었습니다.'); loadCertification(); }
    else if (res.status === 401) { toast('인증 만료', 'error'); doLogout(); }
    else toast('삭제 실패', 'error');
  } catch { toast('오류 발생', 'error'); }
}

/* ─────────────────────────────────────────────
   POINT ITEMS (devPoints / troubleshooting)
───────────────────────────────────────────── */
function getPointItems(type) {
  return type === 'devpoint' ? devPointItems : troubleItems;
}

function renderPointItems(type) {
  const items = getPointItems(type);
  const list  = document.getElementById(type + '-list');
  list.innerHTML = '';
  items.forEach((item, i) => {
    const row = document.createElement('div');
    row.className = 'point-item';

    const labelInput = document.createElement('input');
    labelInput.type        = 'text';
    labelInput.placeholder = '제목 (예: Redis 캐싱)';
    labelInput.value       = item.label;

    const contentArea = document.createElement('textarea');
    contentArea.placeholder = '내용을 입력하세요...';
    contentArea.value       = item.content;

    const imageWrap = document.createElement('div');
    imageWrap.className = 'point-image-wrap';
    imageWrap.style.gridColumn = '1 / 3';
    if (item.imageUrl) {
      const preview = document.createElement('img');
      preview.src = item.imageUrl; preview.className = 'point-img-preview';
      imageWrap.appendChild(preview);
    }
    const imageBtn = document.createElement('label');
    imageBtn.className = 'btn btn-outline btn-sm';
    imageBtn.style.cursor = 'pointer'; imageBtn.style.display = 'inline-block'; imageBtn.style.fontSize = '0.75rem';
    imageBtn.textContent = item.imageUrl ? '이미지 변경' : '이미지 업로드';
    const imageFile = document.createElement('input');
    imageFile.type = 'file'; imageFile.accept = 'image/*'; imageFile.style.display = 'none';
    imageFile.onchange = async (ev) => {
      const f = ev.target.files[0]; if (!f) return;
      const fd = new FormData(); fd.append('file', f); fd.append('path', 'points');
      try {
        const res = await adminFetch(`${API}/api/admin/upload`, {
          method: 'POST', headers: { 'Authorization': 'Bearer ' + localStorage.getItem('token') }, body: fd
        });
        if (res.ok) {
          const { url } = await res.json();
          item.imageUrl = url;
          renderPointItems(type);
          toast('이미지 업로드 완료');
        } else { toast('업로드 실패', 'error'); }
      } catch { toast('업로드 오류', 'error'); }
    };
    imageBtn.appendChild(imageFile);
    imageWrap.appendChild(imageBtn);
    const imageInput = document.createElement('input');
    imageInput.type = 'hidden';
    imageInput.className = 'point-image-input';
    imageInput.value = item.imageUrl || '';

    const removeBtn = document.createElement('button');
    removeBtn.className = 'point-item-remove';
    removeBtn.type      = 'button';
    removeBtn.textContent = '✕';
    removeBtn.onclick   = () => removePointItem(type, i);

    row.appendChild(labelInput);
    row.appendChild(contentArea);
    row.appendChild(imageWrap);
    row.appendChild(imageInput);
    row.appendChild(removeBtn);
    list.appendChild(row);
  });
}

function addPointItem(type) {
  getPointItems(type).push({ label: '', content: '', imageUrl: '' });
  renderPointItems(type);
  const list = document.getElementById(type + '-list');
  const inputs = list.querySelectorAll('input');
  if (inputs.length) inputs[inputs.length - 1].focus();
}

function removePointItem(type, index) {
  getPointItems(type).splice(index, 1);
  renderPointItems(type);
}

function collectPointItems() {
  // DOM을 source of truth로 삼아 배열 완전 재구성
  const devRows = document.querySelectorAll('#devpoint-list .point-item');
  devPointItems = Array.from(devRows).map(row => {
    const inputs = row.querySelectorAll('input');
    return {
      label:    inputs[0].value.trim(),
      content:  row.querySelector('textarea').value.trim(),
      imageUrl: inputs[1] ? inputs[1].value.trim() : '',
    };
  });

  const trRows = document.querySelectorAll('#trouble-list .point-item');
  troubleItems = Array.from(trRows).map(row => {
    const inputs = row.querySelectorAll('input');
    return {
      label:    inputs[0].value.trim(),
      content:  row.querySelector('textarea').value.trim(),
      imageUrl: inputs[1] ? inputs[1].value.trim() : '',
    };
  });
}

/* ─────────────────────────────────────────────
   TAG INPUT
───────────────────────────────────────────── */
function renderTagChips(type) {
  const tags  = type === 'tech' ? techTags : featTags;
  const wrap  = document.getElementById(type + '-tag-wrap');
  const input = document.getElementById(type + '-input');
  const ph    = document.getElementById(type + '-placeholder');

  // Remove old chips (keep input and placeholder)
  wrap.querySelectorAll('.tag-chip').forEach(el => el.remove());

  if (ph) ph.style.display = tags.length === 0 ? '' : 'none';

  tags.forEach((tag, i) => {
    const chip = document.createElement('span');
    chip.className = 'tag-chip';
    chip.innerHTML = `${esc(tag)}<button type="button" onclick="removeTag('${type}', ${i})">✕</button>`;
    wrap.insertBefore(chip, input);
  });
}

function addTag(type, value) {
  const val  = value.trim().replace(/,+$/, '');
  const tags = type === 'tech' ? techTags : featTags;
  if (val && !tags.includes(val)) {
    tags.push(val);
    renderTagChips(type);
  }
}

function removeTag(type, index) {
  if (type === 'tech') techTags.splice(index, 1);
  else                 featTags.splice(index, 1);
  renderTagChips(type);
}

function setupTagInput(type) {
  const input = document.getElementById(type + '-input');
  input.addEventListener('keydown', e => {
    if (e.key === 'Enter' || e.key === ',') {
      e.preventDefault();
      addTag(type, input.value);
      input.value = '';
    } else if (e.key === 'Backspace' && input.value === '') {
      const tags = type === 'tech' ? techTags : featTags;
      if (tags.length > 0) { tags.pop(); renderTagChips(type); }
    }
  });
  input.addEventListener('blur', () => {
    if (input.value.trim()) { addTag(type, input.value); input.value = ''; }
  });
}

/* ─────────────────────────────────────────────
   PAGINATION
───────────────────────────────────────────── */
function renderPagination(type, current, total) {
  const container = document.getElementById(type + '-pagination');
  container.innerHTML = '';
  if (total <= 1) return;

  const prev = document.createElement('button');
  prev.className = 'page-btn';
  prev.textContent = '←';
  prev.disabled = current === 0;
  prev.onclick = () => type === 'projects' ? loadProjects(current - 1) : loadContacts(current - 1);
  container.appendChild(prev);

  for (let i = 0; i < total; i++) {
    const btn = document.createElement('button');
    btn.className = 'page-btn' + (i === current ? ' active' : '');
    btn.textContent = i + 1;
    btn.onclick = ((_i) => () => type === 'projects' ? loadProjects(_i) : loadContacts(_i))(i);
    container.appendChild(btn);
  }

  const next = document.createElement('button');
  next.className = 'page-btn';
  next.textContent = '→';
  next.disabled = current >= total - 1;
  next.onclick = () => type === 'projects' ? loadProjects(current + 1) : loadContacts(current + 1);
  container.appendChild(next);
}

/* ─────────────────────────────────────────────
   UTILS
───────────────────────────────────────────── */
function esc(str) {
  if (str == null) return '';
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}

/* Enter key on login */
document.getElementById('login-password').addEventListener('keydown', e => {
  if (e.key === 'Enter') doLogin();
});
document.getElementById('login-username').addEventListener('keydown', e => {
  if (e.key === 'Enter') document.getElementById('login-password').focus();
});

/* Close modal on backdrop click */
document.getElementById('project-modal').addEventListener('click', e => {
  if (e.target === document.getElementById('project-modal')) closeProjectModal();
});

/* Setup tag inputs */
setupTagInput('tech');
setupTagInput('feat');

/* Auto login check via session cookie */
window.addEventListener('DOMContentLoaded', async () => {
  try {
    const res = await fetch('/api/profile', { credentials: 'include' });
    if (res.ok) {
      const authCheck = await adminFetch('/api/admin/contacts?page=0&size=1', { credentials: 'include' });
      if (authCheck.ok || authCheck.status === 200) {
        document.getElementById('topbar-user').textContent = 'admin';
        showDashboard();
      }
    }
  } catch { /* 로그인 화면 유지 */ }
});

/* ─────────────────────────────────────────────
   CONTENT BLOCKS
───────────────────────────────────────────── */
function renderContentBlocks() {
  const list = document.getElementById('content-block-list');
  list.innerHTML = '';
  contentBlocks.forEach((block, i) => {
    const item = document.createElement('div');
    item.className = 'content-block-item';

    const header = document.createElement('div');
    header.className = 'content-block-header';

    const typeLabel = document.createElement('span');
    typeLabel.className = 'content-block-type';
    typeLabel.textContent = block.blockType === 'IMAGE' ? 'IMAGE' : 'TEXT';

    const actions = document.createElement('div');
    actions.className = 'content-block-actions';
    if (i > 0) {
      const upBtn = document.createElement('button');
      upBtn.textContent = '↑'; upBtn.onclick = () => { moveContentBlock(i, -1); };
      actions.appendChild(upBtn);
    }
    if (i < contentBlocks.length - 1) {
      const downBtn = document.createElement('button');
      downBtn.textContent = '↓'; downBtn.onclick = () => { moveContentBlock(i, 1); };
      actions.appendChild(downBtn);
    }
    const removeBtn = document.createElement('button');
    removeBtn.className = 'block-remove';
    removeBtn.textContent = '✕'; removeBtn.onclick = () => { contentBlocks.splice(i, 1); renderContentBlocks(); };
    actions.appendChild(removeBtn);

    header.appendChild(typeLabel);
    header.appendChild(actions);
    item.appendChild(header);

    if (block.blockType === 'TEXT') {
      const textarea = document.createElement('textarea');
      textarea.value = block.content || '';
      textarea.placeholder = '텍스트를 입력하세요...';
      item.appendChild(textarea);
    } else {
      if (block.content) {
        const img = document.createElement('img');
        img.src = block.content; img.className = 'content-block-img-preview';
        item.appendChild(img);
      }
      const uploadLabel = document.createElement('label');
      uploadLabel.className = 'btn btn-outline btn-sm';
      uploadLabel.style.cursor = 'pointer'; uploadLabel.style.display = 'inline-block';
      uploadLabel.textContent = block.content ? '이미지 변경' : '이미지 업로드';
      const fileInput = document.createElement('input');
      fileInput.type = 'file'; fileInput.accept = 'image/*'; fileInput.style.display = 'none';
      fileInput.onchange = async (ev) => {
        const f = ev.target.files[0]; if (!f) return;
        const fd = new FormData(); fd.append('file', f); fd.append('path', 'content');
        try {
          const res = await adminFetch(`${API}/api/admin/upload`, {
            method: 'POST', headers: { 'Authorization': 'Bearer ' + localStorage.getItem('token') }, body: fd
          });
          if (res.ok) {
            const { url } = await res.json();
            contentBlocks[i].content = url;
            renderContentBlocks();
            toast('이미지 업로드 완료');
          } else { toast('업로드 실패', 'error'); }
        } catch { toast('업로드 오류', 'error'); }
      };
      uploadLabel.appendChild(fileInput);
      item.appendChild(uploadLabel);
    }

    list.appendChild(item);
  });
}

function addContentBlock(type) {
  contentBlocks.push({ blockType: type, content: '' });
  renderContentBlocks();
}

function moveContentBlock(index, dir) {
  const target = index + dir;
  if (target < 0 || target >= contentBlocks.length) return;
  [contentBlocks[index], contentBlocks[target]] = [contentBlocks[target], contentBlocks[index]];
  renderContentBlocks();
}

function collectContentBlocks() {
  const items = document.querySelectorAll('#content-block-list .content-block-item');
  contentBlocks = Array.from(items).map((item, i) => {
    const type = contentBlocks[i]?.blockType || 'TEXT';
    if (type === 'TEXT') {
      const ta = item.querySelector('textarea');
      return { blockType: 'TEXT', content: ta ? ta.value.trim() : '' };
    }
    return { blockType: 'IMAGE', content: contentBlocks[i]?.content || '' };
  });
}

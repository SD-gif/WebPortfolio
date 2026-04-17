const API_BASE = '';

/* ── Navbar: scroll effect ── */
const navbar = document.getElementById('navbar');
window.addEventListener('scroll', () => {
  navbar.classList.toggle('scrolled', window.scrollY > 40);
});

/* ── Navbar: active section highlight ── */
const navLinks = document.querySelectorAll('.nav-links a[data-section]');
const sectionObserver = new IntersectionObserver(entries => {
  entries.forEach(e => {
    if (e.isIntersecting) {
      navLinks.forEach(a => a.classList.remove('active'));
      const link = document.querySelector(`.nav-links a[data-section="${e.target.id}"]`);
      if (link) link.classList.add('active');
    }
  });
}, { threshold: 0.4 });
['projects','experience','skills','about','edu-cert','contact'].forEach(id => {
  const el = document.getElementById(id);
  if (el) sectionObserver.observe(el);
});

/* ── Mobile nav ── */
document.getElementById('hamburger').addEventListener('click', () => {
  document.getElementById('nav-links').classList.toggle('open');
});
document.querySelectorAll('.nav-links a').forEach(a => {
  a.addEventListener('click', () => document.getElementById('nav-links').classList.remove('open'));
});
document.addEventListener('click', e => {
  const nav = document.getElementById('nav-links');
  if (nav.classList.contains('open') && !nav.contains(e.target) && !document.getElementById('hamburger').contains(e.target)) {
    nav.classList.remove('open');
  }
});

/* ── Scroll fade-in ── */
const observer = new IntersectionObserver(entries => {
  entries.forEach(e => { if (e.isIntersecting) e.target.classList.add('visible'); });
}, { threshold: 0.1 });
document.querySelectorAll('.fade-in').forEach(el => observer.observe(el));

/* ── Profile ── */
async function fetchProfile() {
  try {
    const res = await fetch(API_BASE + '/api/profile');
    if (!res.ok) return;
    const { name, role, bio, expYears, projectCount, email, githubUrl } = await res.json();

    if (role) {
      const aboutRole = document.getElementById('about-role');
      if (aboutRole) aboutRole.textContent = role;
    }
    if (bio) {
      const aboutBio = document.getElementById('about-bio-text');
      if (aboutBio) aboutBio.textContent = bio;
    }
    if (expYears !== undefined) {
      const v = expYears + '년';
      const aboutExp = document.getElementById('about-stat-exp');
      if (aboutExp) aboutExp.textContent = v;
    }
    if (projectCount !== undefined) {
      const v = projectCount + '+';
      const aboutProj = document.getElementById('about-stat-projects');
      if (aboutProj) aboutProj.textContent = v;
    }
    if (email) {
      const aboutEmail = document.getElementById('about-email');
      if (aboutEmail) aboutEmail.textContent = email;
      const contactEmail = document.getElementById('contact-email-text');
      if (contactEmail) contactEmail.textContent = email;
      const socialEmail = document.getElementById('social-email');
      if (socialEmail) socialEmail.href = 'mailto:' + email;
    }
    if (githubUrl) {
      const socialGithub = document.getElementById('social-github');
      if (socialGithub) socialGithub.href = githubUrl;
      const aboutGithub = document.getElementById('about-github');
      if (aboutGithub) { aboutGithub.href = githubUrl; aboutGithub.textContent = githubUrl.replace('https://', ''); }
      const footerGithub = document.getElementById('footer-github');
      if (footerGithub) footerGithub.href = githubUrl;
    }
  } catch (e) { console.error('fetchProfile', e); }
}

/* ── Projects Grid ── */
async function fetchProjects() {
  const grid = document.getElementById('projects-grid');
  try {
    const res = await fetch(API_BASE + '/api/projects?page=0&size=20');
    if (!res.ok) throw new Error();
    const { content = [] } = await res.json();

    if (!content.length) {
      grid.innerHTML = '<p style="color:var(--muted)">등록된 프로젝트가 없습니다.</p>';
      return;
    }

    grid.innerHTML = '';
    content.forEach(({ id, title, summary, techStack = [] }) => {
      const card = document.createElement('div');
      card.className = 'proj-card fade-in';
      const tagsHtml = techStack.slice(0, 2).map(t => `<span class="proj-tag">${escapeHtml(t)}</span>`).join('');
      const moreTag  = techStack.length > 2 ? `<span class="proj-tag">+${techStack.length - 2}</span>` : '';
      card.innerHTML = `
        <div class="proj-card-top">
          <div class="proj-title">${escapeHtml(title) || ''}</div>
        </div>
        <p class="proj-desc">${escapeHtml(summary) || ''}</p>
        <div class="proj-card-bottom">
          <div class="proj-tags">${tagsHtml}${moreTag}</div>
          <span class="proj-more"><svg viewBox="0 0 24 24" width="16" height="16"><path d="M5 12h14M12 5l7 7-7 7" stroke="currentColor" fill="none" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg></span>
        </div>
      `;
      card.addEventListener('click', () => { location.href = '/project.html?id=' + id; });
      grid.appendChild(card);
      observer.observe(card);
    });
  } catch (e) {
    grid.innerHTML = '<p style="color:var(--muted)">프로젝트를 불러오지 못했습니다.</p>';
  }
}

/* ── Skills ── */
async function fetchSkills() {
  const grid = document.getElementById('skills-grid');
  try {
    const res = await fetch(API_BASE + '/api/skills');
    if (!res.ok) throw new Error();
    const { categories = [] } = await res.json();
    grid.innerHTML = '';
    categories.forEach(({ category, skills = [] }) => {
      const rows = skills.map(s => {
        const lv = Math.min(Math.max(s.level || 1, 1), 4);
        const bars = Array.from({length: 4}, (_, i) =>
          `<span class="seg ${i < lv ? 'seg-fill seg-l' + lv : 'seg-empty'}"></span>`
        ).join('');
        return `
          <div class="skill-row">
            <span class="skill-name">${escapeHtml(s.name)}</span>
            <div class="skill-seg">${bars}</div>
          </div>`;
      }).join('');
      const card = document.createElement('div');
      card.className = 'skill-card';
      card.innerHTML = `
        <div class="skill-category-header">
          <span class="skill-category">${escapeHtml(category)}</span>
        </div>
        <div class="skill-list">${rows}</div>
      `;
      grid.appendChild(card);
    });
  } catch (e) { console.error('fetchSkills', e); }
}

/* ── Education ── */
async function fetchEducation() {
  const el = document.getElementById('edu-list');
  try {
    const list = await fetch('/api/educations').then(r => r.json());
    if (!list.length) { el.innerHTML = '<p style="color:var(--muted);font-size:.85rem">등록된 학력 정보가 없습니다.</p>'; return; }
    el.innerHTML = list.map(e => `
      <div class="edu-item">
        <div class="edu-degree">${escapeHtml(e.degree)}</div>
        <div class="edu-school">${escapeHtml(e.institution)}${e.major ? ' · ' + escapeHtml(e.major) : ''}</div>
        <div class="edu-period">${e.startDate ?? ''}${e.endDate ? ' — ' + e.endDate : ''}</div>
      </div>`).join('');
  } catch { el.innerHTML = '<p style="color:var(--muted);font-size:.85rem">불러오기 실패</p>'; }
}

/* ── Certifications ── */
async function fetchCertifications() {
  const el = document.getElementById('cert-list');
  try {
    const list = await fetch('/api/certifications').then(r => r.json());
    if (!list.length) { el.innerHTML = '<p style="color:var(--muted);font-size:.85rem">등록된 자격증/수상 정보가 없습니다.</p>'; return; }
    const certs  = list.filter(c => c.type === 'CERT');
    const awards = list.filter(c => c.type === 'AWARD');
    el.innerHTML = [
      ...certs.map(c => `
        <div class="cert-item">
          <div class="cert-name">${escapeHtml(c.name)}</div>
          ${c.issuer ? `<div class="cert-issuer">${escapeHtml(c.issuer)}</div>` : ''}
          ${c.acquiredDate ? `<div class="cert-date">${escapeHtml(c.acquiredDate)}</div>` : ''}
        </div>`),
      awards.length ? `<div style="margin-top:1rem;display:flex;flex-wrap:wrap;">${awards.map(a =>
        `<span class="award-badge">${escapeHtml(a.name)}${a.acquiredDate ? ' ' + escapeHtml(a.acquiredDate) : ''}</span>`
      ).join('')}</div>` : '',
    ].join('');
  } catch { el.innerHTML = '<p style="color:var(--muted);font-size:.85rem">불러오기 실패</p>'; }
}

/* ── Experience ── */
function escapeHtml(s) {
  return (s == null ? '' : String(s))
    .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;').replace(/'/g, '&#39;');
}
let expSlideIndex = 0;
let expSlideCount = 0;

async function fetchExperiences() {
  const container = document.getElementById('experience-grid');
  try {
    const res = await fetch(API_BASE + '/api/experiences');
    if (!res.ok) throw new Error();
    const list = await res.json();
    if (!list.length) {
      container.innerHTML = '<p style="color:var(--muted);font-size:.875rem">등록된 역량이 없습니다.</p>';
      return;
    }
    expSlideCount = list.length;
    expSlideIndex = 0;

    const slides = list.map(exp => {
      const stack = (exp.techStack || []).map(t => `<span class="exp-stack-tag">${escapeHtml(t)}</span>`).join('');
      const imgHtml = exp.imageUrl
        ? `<div class="exp-slide-image"><img src="${escapeHtml(exp.imageUrl)}" alt="${escapeHtml(exp.title)}" loading="lazy" /></div>` : '';
      return `
        <div class="exp-slide">
          <div class="exp-slide-inner">
            <div class="exp-slide-header">
              <h3 class="exp-slide-title">${escapeHtml(exp.title)}</h3>
              ${exp.summary ? `<p class="exp-slide-summary">${escapeHtml(exp.summary)}</p>` : ''}
            </div>
            ${imgHtml}
            <div class="exp-slide-sections">
              <div class="exp-slide-section section-situation">
                <span class="exp-slide-label">상황</span>
                <p class="exp-slide-text">${escapeHtml(exp.situation)}</p>
              </div>
              <div class="exp-slide-section section-approach">
                <span class="exp-slide-label">접근</span>
                <p class="exp-slide-text">${escapeHtml(exp.approach)}</p>
              </div>
              <div class="exp-slide-section section-learned">
                <span class="exp-slide-label">배운 점</span>
                <p class="exp-slide-text">${escapeHtml(exp.learned)}</p>
              </div>
            </div>
            ${stack ? `<div class="exp-slide-stack">${stack}</div>` : ''}
          </div>
        </div>`;
    }).join('');

    const dots = list.map((_, i) =>
      `<button class="exp-dot${i === 0 ? ' active' : ''}" onclick="expGoTo(${i})"></button>`
    ).join('');

    container.innerHTML = `
      <div class="exp-slider">
        <div class="exp-track" id="exp-track">${slides}</div>
        <button class="exp-arrow exp-arrow-left" onclick="expMove(-1)">
          <svg viewBox="0 0 24 24" width="20" height="20"><path d="M15 18l-6-6 6-6" stroke="currentColor" fill="none" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
        </button>
        <button class="exp-arrow exp-arrow-right" onclick="expMove(1)">
          <svg viewBox="0 0 24 24" width="20" height="20"><path d="M9 18l6-6-6-6" stroke="currentColor" fill="none" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
        </button>
      </div>
      <div class="exp-dots" id="exp-dots">${dots}</div>
      <div class="exp-counter"><span id="exp-counter-current">1</span> / ${list.length}</div>
    `;
  } catch (e) {
    container.innerHTML = '<p style="color:var(--muted);font-size:.875rem">역량 정보를 불러오지 못했습니다.</p>';
  }
}

function expGoTo(idx) {
  expSlideIndex = Math.max(0, Math.min(idx, expSlideCount - 1));
  const track = document.getElementById('exp-track');
  if (track) track.style.transform = `translateX(-${expSlideIndex * 100}%)`; // each slide is 100% of track item
  document.querySelectorAll('.exp-dot').forEach((d, i) => d.classList.toggle('active', i === expSlideIndex));
  const counter = document.getElementById('exp-counter-current');
  if (counter) counter.textContent = expSlideIndex + 1;
}
function expMove(dir) { expGoTo(expSlideIndex + dir); }

/* ── Contact form ── */
async function handleSubmit(e) {
  e.preventDefault();
  const name    = document.getElementById('input-name').value.trim();
  const email   = document.getElementById('input-email').value.trim();
  const message = document.getElementById('input-message').value.trim();
  const btn     = document.getElementById('form-submit');
  btn.disabled = true; btn.textContent = '전송 중...';
  try {
    const res = await fetch(API_BASE + '/api/contact', {
      method: 'POST', headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name, email, message }),
    });
    const result = await res.json();
    if (res.ok && result.success) {
      document.getElementById('contact-form').outerHTML =
        `<div class="form-success">메시지가 전송되었습니다.<br><span style="font-size:.8rem;opacity:.7">${result.message || '빠른 시일 내에 답변 드리겠습니다.'}</span></div>`;
    } else if (res.status === 429) {
      btn.disabled = false; btn.textContent = '메시지 보내기';
      alert('잠시 후 다시 시도해주세요. (분당 3회 제한)');
    } else {
      btn.disabled = false; btn.textContent = '메시지 보내기';
      alert(result.message || '오류가 발생했습니다. 다시 시도해주세요.');
    }
  } catch {
    btn.disabled = false; btn.textContent = '메시지 보내기';
    alert('네트워크 오류가 발생했습니다.');
  }
}

/* ── Init ── */
document.addEventListener('DOMContentLoaded', () => {
  Promise.all([
    fetchProfile(),
    fetchProjects(),
    fetchExperiences(),
    fetchSkills(),
    fetchEducation(),
    fetchCertifications(),
  ]);
});

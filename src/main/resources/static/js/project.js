const API_BASE = '';

function escapeHtml(s) {
  return (s == null ? '' : String(s))
    .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;').replace(/'/g, '&#39;');
}

function openLightbox(src) {
  document.getElementById('lightbox-img').src = src;
  document.getElementById('lightbox').classList.add('open');
  document.body.style.overflow = 'hidden';
}
function closeLightbox() {
  document.getElementById('lightbox').classList.remove('open');
  document.body.style.overflow = '';
}
document.addEventListener('keydown', e => {
  if (e.key === 'Escape') closeLightbox();
});

async function loadProject() {
  const id = new URLSearchParams(location.search).get('id');
  const main = document.getElementById('project-content');

  if (!id) {
    main.innerHTML = '<div class="loading">프로젝트 ID가 없습니다.</div>';
    return;
  }

  try {
    const res = await fetch(API_BASE + '/api/projects/' + id);
    if (!res.ok) throw new Error();
    const data = await res.json();
    document.title = escapeHtml(data.title) + ' | Portfolio';
    render(main, data);
  } catch {
    main.innerHTML = '<div class="loading">프로젝트를 불러오지 못했습니다.</div>';
  }
}

function render(el, { title, summary, description, techStack = [], features = [], devPoints = [], troubleshooting = [], contentBlocks = [], githubUrl, demoUrl, duration, teamSize, role, media = [] }) {
  // Header
  const tagsHtml = techStack.map(t => `<span class="proj-tag">${escapeHtml(t)}</span>`).join('');
  const linksHtml = `
    ${githubUrl ? `<a class="proj-link proj-link-github" href="${escapeHtml(githubUrl)}" target="_blank" rel="noopener">GitHub</a>` : ''}
    ${demoUrl ? `<a class="proj-link proj-link-demo" href="${escapeHtml(demoUrl)}" target="_blank" rel="noopener">Live Demo</a>` : ''}
  `;

  // Meta info
  const metaItems = [
    duration && `<span class="proj-meta-item"><strong>기간</strong> ${escapeHtml(duration)}</span>`,
    teamSize && `<span class="proj-meta-item"><strong>팀</strong> ${escapeHtml(teamSize)}</span>`,
    role && `<span class="proj-meta-item"><strong>역할</strong> ${escapeHtml(role)}</span>`,
  ].filter(Boolean);
  const metaHtml = metaItems.length ? `<div class="proj-meta-info">${metaItems.join('')}</div>` : '';

  // Content blocks
  const blocksHtml = contentBlocks.length ? `
    <hr class="section-divider">
    ${contentBlocks.map(b => b.blockType === 'IMAGE'
      ? `<div class="content-block content-block-image" onclick="openLightbox('${escapeHtml(b.content)}')"><img src="${escapeHtml(b.content)}" alt="프로젝트 이미지" loading="lazy" /></div>`
      : `<div class="content-block content-block-text">${escapeHtml(b.content)}</div>`
    ).join('')}` : '';

  // Overview media gallery
  const galleryHtml = media.length ? `
    <div class="proj-gallery">
      <div class="proj-gallery-grid">
        ${media.map(m => m.mediaType === 'VIDEO'
          ? `<div class="proj-gallery-item"><video controls preload="metadata"><source src="${escapeHtml(m.url)}"></video></div>`
          : `<div class="proj-gallery-item" onclick="openLightbox('${escapeHtml(m.url)}')"><img src="${escapeHtml(m.url)}" alt="프로젝트 이미지" loading="lazy" /></div>`
        ).join('')}
      </div>
    </div>` : '';

  // Features
  const featuresHtml = features.length ? `
    <hr class="section-divider">
    <div class="section-label">주요 기능</div>
    <ul class="proj-features">
      ${features.map(f => `<li>${escapeHtml(f)}</li>`).join('')}
    </ul>` : '';

  // Dev Points
  const devPointsHtml = devPoints.length ? `
    <hr class="section-divider">
    <div class="section-label">개발 포인트</div>
    ${devPoints.map((d, i) => renderPoint(d, i, false)).join('')}` : '';

  // Troubleshooting
  const troubleHtml = troubleshooting.length ? `
    <hr class="section-divider">
    <div class="trouble-section">
      <div class="section-label">트러블슈팅</div>
      ${troubleshooting.map((t, i) => renderPoint(t, i, true)).join('')}
    </div>` : '';

  el.innerHTML = `
    <div class="proj-header-wrap">
      <div class="proj-header">
        <h1 class="proj-title">${escapeHtml(title)}</h1>
        ${summary ? `<p class="proj-summary">${escapeHtml(summary)}</p>` : ''}
        ${metaHtml}
        <div class="proj-meta">
          <div class="proj-tags">${tagsHtml}</div>
          <div class="proj-links">${linksHtml}</div>
        </div>
      </div>
    </div>

    <div class="proj-body">
      ${description ? `
      <div class="section-label">Overview</div>
      <div class="proj-overview">${escapeHtml(description)}</div>
      <hr class="section-divider">` : ''}

      ${blocksHtml}
      ${galleryHtml}
      ${featuresHtml}
      ${devPointsHtml}
      ${troubleHtml}
    </div>

    <div id="related-projects"></div>
  `;

  loadRelatedProjects(el.querySelector('#related-projects'));
}

async function loadRelatedProjects(container) {
  const currentId = new URLSearchParams(location.search).get('id');
  try {
    const res = await fetch('/api/projects?page=0&size=20');
    if (!res.ok) return;
    const { content = [] } = await res.json();
    const others = content.filter(p => String(p.id) !== currentId).slice(0, 3);
    if (!others.length) return;

    const cards = others.map(p => {
      const tags = (p.techStack || []).slice(0, 2).map(t => `<span class="proj-tag">${escapeHtml(t)}</span>`).join('');
      return `
        <a class="related-card" href="/project.html?id=${p.id}">
          <div class="related-title">${escapeHtml(p.title)}</div>
          <div class="related-summary">${escapeHtml(p.summary || '')}</div>
          <div class="related-tags">${tags}</div>
        </a>`;
    }).join('');

    container.innerHTML = `
      <hr class="section-divider">
      <div class="section-label">다른 프로젝트</div>
      <div class="related-grid">${cards}</div>
    `;
  } catch { /* ignore */ }
}

function renderPoint(point, index, isTrouble) {
  const labelClass = isTrouble ? 'point-label trouble-label' : 'point-label';

  if (point.imageUrl) {
    const reverse = index % 2 === 1 ? ' reverse' : '';
    return `
      <div class="point-block">
        <div class="point-with-image${reverse}">
          <div class="point-image" onclick="openLightbox('${escapeHtml(point.imageUrl)}')">
            <img src="${escapeHtml(point.imageUrl)}" alt="${escapeHtml(point.label)}" loading="lazy" />
          </div>
          <div>
            <div class="${labelClass}">${escapeHtml(point.label)}</div>
            <div class="point-content">${escapeHtml(point.content)}</div>
          </div>
        </div>
      </div>`;
  }

  return `
    <div class="point-block">
      <div class="${labelClass}">${escapeHtml(point.label)}</div>
      <div class="point-content">${escapeHtml(point.content)}</div>
    </div>`;
}

document.addEventListener('DOMContentLoaded', loadProject);

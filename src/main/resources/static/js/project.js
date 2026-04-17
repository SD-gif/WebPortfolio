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

function render(el, { title, summary, description, techStack = [], features = [], devPoints = [], troubleshooting = [], githubUrl, demoUrl, media = [] }) {
  // Header
  const tagsHtml = techStack.map(t => `<span class="proj-tag">${escapeHtml(t)}</span>`).join('');
  const linksHtml = `
    ${githubUrl ? `<a class="proj-link proj-link-github" href="${escapeHtml(githubUrl)}" target="_blank" rel="noopener">GitHub</a>` : ''}
    ${demoUrl ? `<a class="proj-link proj-link-demo" href="${escapeHtml(demoUrl)}" target="_blank" rel="noopener">Live Demo</a>` : ''}
  `;

  // Overview media gallery
  const galleryHtml = media.length ? `
    <div class="proj-gallery">
      <div class="proj-gallery-grid">
        ${media.map(m => m.mediaType === 'VIDEO'
          ? `<div class="proj-gallery-item"><video controls preload="metadata"><source src="${m.url}"></video></div>`
          : `<div class="proj-gallery-item" onclick="openLightbox('${m.url}')"><img src="${m.url}" alt="프로젝트 이미지" loading="lazy" /></div>`
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
    <div class="proj-header">
      <h1 class="proj-title">${escapeHtml(title)}</h1>
      ${summary ? `<p class="proj-summary">${escapeHtml(summary)}</p>` : ''}
      <div class="proj-meta">
        <div class="proj-tags">${tagsHtml}</div>
        <div class="proj-links">${linksHtml}</div>
      </div>
    </div>

    ${description ? `
    <hr class="section-divider">
    <div class="section-label">Overview</div>
    <div class="proj-overview">${escapeHtml(description)}</div>` : ''}

    ${galleryHtml}
    ${featuresHtml}
    ${devPointsHtml}
    ${troubleHtml}
  `;
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

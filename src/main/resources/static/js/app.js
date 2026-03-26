// --- Aplicacion principal: navegacion, tema, sidebar ---

// --- Invalidacion de caches globales (se ejecuta al navegar entre paginas) ---
function invalidateAllCaches() {
    if (typeof _institucionesCache !== 'undefined') _institucionesCache = null;
    if (typeof _carrerasCache !== 'undefined') _carrerasCache = null;
    if (typeof _cursosCache !== 'undefined') _cursosCache = null;
    if (typeof _materiasCache !== 'undefined') _materiasCache = null;
    if (typeof _usuariosCache !== 'undefined') _usuariosCache = null;
    if (typeof _profesoresCache !== 'undefined') _profesoresCache = null;
    if (typeof _alumnosCache !== 'undefined') _alumnosCache = null;
    if (typeof _cursoMateriasCache !== 'undefined') _cursoMateriasCache = null;
    if (typeof _asignacionesCache !== 'undefined') _asignacionesCache = null;
}

const App = {
    currentPage: 'dashboard',
    _initialized: false, // Evita re-bindear eventos al volver del login

    // --- Inicializacion: comprueba autenticacion y arranca la app ---
    async init() {
        // Compuerta de auth: redirige a login si no hay sesion
        if (!Auth.isLoggedIn()) {
            Auth.showAuthScreen();
            return;
        }

        // Valida token con el backend
        const valid = await Auth.validate();
        if (!valid) {
            Auth.showAuthScreen();
            return;
        }

        // Show the app
        Auth.showApp();

        // Bindea eventos solo una vez
        if (!this._initialized) {
            this.initTheme();
            this.initSidebar();
            this.initNavigation();
            this.initLogout();
            this._initialized = true;
        }

        // Restaura pagina desde hash de URL o usa dashboard por defecto
        const hash = location.hash.replace('#', '');
        const validPages = ['dashboard','administradores','profesores','alumnos','carreras','cursos','materias','asignaciones','horarios','asistencias','inscripciones','reconocimiento','enrolamiento'];
        const startPage = validPages.includes(hash) ? hash : 'dashboard';
        this.navigate(startPage);

        // Maneja navegacion con botones atras/adelante del navegador
        window.addEventListener('hashchange', () => {
            const page = location.hash.replace('#', '');
            if (validPages.includes(page) && page !== this.currentPage) {
                this.navigate(page);
            }
        });
    },

    // --- Toggle de tema claro/oscuro (persiste en localStorage) ---
    initTheme() {
        const saved = localStorage.getItem('theme') || 'dark';
        document.documentElement.setAttribute('data-theme', saved);
        this.updateThemeIcon(saved);

        document.getElementById('theme-toggle').addEventListener('click', () => {
            const current = document.documentElement.getAttribute('data-theme');
            const next = current === 'dark' ? 'light' : 'dark';
            document.documentElement.setAttribute('data-theme', next);
            localStorage.setItem('theme', next);
            this.updateThemeIcon(next);
        });
    },

    updateThemeIcon(theme) {
        const icon = document.querySelector('#theme-toggle i');
        icon.className = theme === 'dark' ? 'bi bi-sun' : 'bi bi-moon-stars';
    },

    // --- Sidebar colapsable (persiste en localStorage) ---
    initSidebar() {
        const sidebar = document.getElementById('sidebar');
        const saved = localStorage.getItem('sidebar-collapsed');
        if (saved === 'true') sidebar.classList.add('collapsed');

        document.getElementById('sidebar-toggle').addEventListener('click', () => {
            sidebar.classList.toggle('collapsed');
            localStorage.setItem('sidebar-collapsed', sidebar.classList.contains('collapsed'));
        });
    },

    // --- Navegacion: click en items del sidebar ---
    initNavigation() {
        document.querySelectorAll('.nav-item[data-page]').forEach(item => {
            item.addEventListener('click', () => {
                this.navigate(item.dataset.page);
            });
        });
    },

    // --- Boton de cerrar sesion ---
    initLogout() {
        document.getElementById('logout-btn').addEventListener('click', () => {
            Auth.logout();
        });
    },

    // --- Navegacion/routing: actualiza hash, breadcrumb, carga pagina ---
    navigate(page) {
        this.currentPage = page;

        // Persiste pagina actual en el hash de la URL
        if (location.hash !== '#' + page) {
            history.replaceState(null, '', '#' + page);
        }

        // Invalida caches para que las paginas carguen datos frescos
        invalidateAllCaches();

        // Marca item activo en el sidebar
        document.querySelectorAll('.nav-item').forEach(item => {
            item.classList.toggle('active', item.dataset.page === page);
        });

        // Actualiza breadcrumb con nombre legible de la pagina
        const labels = {
            'dashboard': 'Dashboard',
            'administradores': 'Administradores',
            'profesores': 'Profesores',
            'alumnos': 'Alumnos',
            'carreras': 'Carreras',
            'cursos': 'Cursos',
            'materias': 'Materias',
            'asignaciones': 'Cargos por Profesor',
            'horarios': 'Horarios',
            'asistencias': 'Asistencias',
            'inscripciones': 'Inscripciones Alumnos',
            'reconocimiento': 'Reconocimiento Facial',
            'enrolamiento': 'Carga Biometrica'
        };
        UI.setBreadcrumb(['Inicio', labels[page] || page]);

        // Carga el modulo de pagina correspondiente
        UI.showLoading();
        const pages = {
            'dashboard': () => DashboardPage.render(),
            'administradores': () => AdministradoresPage.render(),
            'profesores': () => ProfesoresPage.render(),
            'alumnos': () => AlumnosPage.render(),
            'carreras': () => CarrerasPage.render(),
            'cursos': () => CursosPage.render(),
            'materias': () => MateriasPage.render(),
            'asignaciones': () => AsignacionesPage.render(),
            'horarios': () => HorariosPage.render(),
            'asistencias': () => AsistenciasPage.render(),
            'inscripciones': () => InscripcionesPage.render(),
            'reconocimiento': () => ReconocimientoPage.render(),
            'enrolamiento': () => EnrolamientoPage.render()
        };

        if (pages[page]) {
            pages[page]();
        }
    }
};

// --- Punto de entrada: aplica tema guardado e inicia la app ---
document.addEventListener('DOMContentLoaded', () => {
    const savedTheme = localStorage.getItem('theme') || 'dark';
    document.documentElement.setAttribute('data-theme', savedTheme);
    App.init();
});

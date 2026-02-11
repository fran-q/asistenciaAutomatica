// App - Navigation, Theme, Sidebar
const App = {
    currentPage: 'dashboard',

    init() {
        this.initTheme();
        this.initSidebar();
        this.initNavigation();
        this.navigate('dashboard');
    },

    // Theme toggle
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

    // Sidebar collapse
    initSidebar() {
        const sidebar = document.getElementById('sidebar');
        const saved = localStorage.getItem('sidebar-collapsed');
        if (saved === 'true') sidebar.classList.add('collapsed');

        document.getElementById('sidebar-toggle').addEventListener('click', () => {
            sidebar.classList.toggle('collapsed');
            localStorage.setItem('sidebar-collapsed', sidebar.classList.contains('collapsed'));
        });
    },

    // Navigation
    initNavigation() {
        document.querySelectorAll('.nav-item[data-page]').forEach(item => {
            item.addEventListener('click', () => {
                this.navigate(item.dataset.page);
            });
        });
    },

    navigate(page) {
        this.currentPage = page;

        // Update active nav
        document.querySelectorAll('.nav-item').forEach(item => {
            item.classList.toggle('active', item.dataset.page === page);
        });

        // Breadcrumb
        const labels = {
            'dashboard': 'Dashboard',
            'usuarios': 'Usuarios',
            'profesores': 'Profesores',
            'alumnos': 'Alumnos',
            'instituciones': 'Instituciones',
            'carreras': 'Carreras',
            'cursos': 'Cursos',
            'materias': 'Materias',
            'curso-materias': 'Curso-Materia',
            'asignaciones': 'Asignaciones',
            'horarios': 'Horarios',
            'asistencias': 'Asistencias',
            'inscripciones': 'Inscripciones'
        };
        UI.setBreadcrumb(['Inicio', labels[page] || page]);

        // Load page
        UI.showLoading();
        const pages = {
            'dashboard': () => DashboardPage.render(),
            'usuarios': () => UsuariosPage.render(),
            'profesores': () => ProfesoresPage.render(),
            'alumnos': () => AlumnosPage.render(),
            'instituciones': () => InstitucionesPage.render(),
            'carreras': () => CarrerasPage.render(),
            'cursos': () => CursosPage.render(),
            'materias': () => MateriasPage.render(),
            'curso-materias': () => CursoMateriasPage.render(),
            'asignaciones': () => AsignacionesPage.render(),
            'horarios': () => HorariosPage.render(),
            'asistencias': () => AsistenciasPage.render(),
            'inscripciones': () => InscripcionesPage.render()
        };

        if (pages[page]) {
            pages[page]();
        }
    }
};

document.addEventListener('DOMContentLoaded', () => App.init());

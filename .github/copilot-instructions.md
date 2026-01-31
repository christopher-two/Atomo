# Instrucciones para Copilot (proyecto Atomo)

Resumen rápido
- Proyecto Android escrito en Kotlin. UI con Jetpack Compose (Material3). Persistencia con Room. Uso extensivo de coroutines y Flows. Carga de imágenes con Coil. Sigue la guía de arquitectura en `docs/ARCHITECTURE.md`.

Qué debe priorizar Copilot al sugerir completaciones
- Mantener la consistencia con Kotlin (nombres camelCase, classes/objects en PascalCase, funciones cortas y expresivas).
- Preferir APIs de alto nivel que el proyecto ya usa: coroutines + Flow, Room DAO flows, ViewModel + viewModelScope, Compose + LaunchedEffect cuando sea apropiado.
- Capitalizar patrones de la arquitectura existente: separar Data / Domain / Presentation, usar DAOs y repositorios, exponer Flows desde la capa local.
- Usar Material3 y componentes Compose ya presentes (no introducir bibliotecas de UI nuevas sin revisión).

Buenas prácticas y restricciones específicas del repo
- No añadas dependencias nuevas en las sugerencias sin que el cambio venga acompañado de una nota sobre por qué y cómo actualizar `build.gradle.kts` y `gradle/libs.versions.toml`.
- No exponer secretos ni claves (ej.: `Keys.jks`, `keystore.properties`). Evita sugerencias que impriman o suban datos sensibles.
- Prefiere soluciones que no rompan la compatibilidad: sugiere cambios en pequeños commits/PRs y agrega pruebas cuando el cambio afecta lógica.
- Cuando sugieras modificaciones en la base de datos (Room), incluye la migración correspondiente o sugiere usar una DB in-memory para pruebas.

Consejos prácticos para Compose
- Evita lanzar coroutines directamente dentro de composables (no usar `GlobalScope.launch` o `CoroutineScope().launch` durante la composición). Usa `LaunchedEffect`, `rememberCoroutineScope()` o delega a `ViewModel`.
- Mantén los composables sin efectos secundarios: recibir estado inmutable y callbacks que emiten eventos.
- Prefiere `State`/`MutableState` y `collectAsState()` para observar Flows en la UI, y `remember`/`derivedStateOf` para cálculos derivados.
- Ofrece previews cuando añadas UI nueva.

Coroutines y Flows
- Cuando sugieras colecciones de Flow en la UI, usa `collectAsState()` o `collectLatest` dentro de `LaunchedEffect` con una clave apropiada.
- Para operaciones de larga duración en el dominio/repositorio, recomienda `suspend` functions y exponer resultados a través de Flow o Result-wrapping (sealed classes) para estado de carga/éxito/error.

Room y pruebas
- Cuando modifiques DAOs o entidades, sugiere tests unitarios con `Room.inMemoryDatabaseBuilder` (hay tests de ejemplo en `app/src/androidTest/.../data/local/dao`).
- Si propones cambios que requieren migraciones, sugiere agregar una migración y pruebas que verifican la migración.

Estilo de commits y PRs (sugerencias que puede ofrecer Copilot)
- Mantén commits pequeños y con título en inglés/español claro: "feat: añadir X" / "fix: corregir Y".
- Al sugerir cambios grandes, incluye un resumen corto: qué cambia, por qué, riesgos y pasos para probar manualmente.

Testing
- Sugiere test unitarios para lógica de negocio y pruebas instrumentadas (androidTest) para DAOs cuando corresponda.
- Es preferible proponer un test por caso: happy path + 1-2 casos límite (nulos, listas vacías, errores de red si aplica).

Documentación y mantenimiento
- Si Copilot genera una nueva feature, sugiere actualizar `docs/ARCHITECTURE.md` y `README.md` con notas breves.
- Cuando agregues APIs públicas o contratos (por ejemplo funciones en repositorios), sugiere ejemplos de uso y tests.

Comportamiento cuando no estés seguro
- Si falta contexto (por ejemplo: no queda claro si usar DI o instanciación manual en un archivo), Copilot debe preferir no hacer cambios disruptivos. Sugerir el cambio en un comentario TODO o en un PR description y dejar la implementación para revisión.

Reglas rápidas de estilo
- Kotlin: usa `val` por defecto, evita `!!`, prefiere safe-calls `?.` y operadores de alcance (`let`, `also`, `run`) de forma clara.
- Null-safety: si una variable puede ser nula, documentar la razón en un comentario breve.
- Nombres: variables y parámetros en español solo cuando el dominio lo requiera; por defecto usar inglés para identificadores técnicos.

Ejemplos de prompts útiles (para desarrolladores que invocan Copilot)
- "Implementa la función suspend que obtiene el perfil desde el repositorio y expone un Flow de UiState (loading/success/error)."
- "Crea un DAO de Room para la entidad X con operaciones insert/update/delete y un test in-memory." 
- "Refactoriza este composable para que sea stateless y reciba los callbacks necesarios." 

Lectura previa obligatoria
- Antes de proponer refactorings o patrones nuevos, Copilot debe basarse en `docs/ARCHITECTURE.md` y en los archivos relevantes bajo `app/src/main/java/org/override/atomo`.

Contacto y revisión humana
- Siempre sugiere abrir un PR para cambios no triviales y añadir una descripción que incluya cómo reproducir y probar.

Gracias por ayudar a mantener la coherencia del proyecto Atomo. Sigue estas pautas para que las sugerencias sean seguras, revisables y fáciles de integrar.

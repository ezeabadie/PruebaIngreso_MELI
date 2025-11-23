# 🗄️ ESQUEMA DE BASE DE DATOS - H2

## 📊 Estructura de la Tabla

El proyecto usa **H2 Database** (base de datos en memoria) con una sola tabla:

```sql
CREATE TABLE dna_records (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    dna_hash    VARCHAR(64) UNIQUE NOT NULL,
    is_mutant   BOOLEAN NOT NULL,
    created_at  TIMESTAMP NOT NULL
);

CREATE INDEX idx_dna_hash ON dna_records(dna_hash);
CREATE INDEX idx_is_mutant ON dna_records(is_mutant);
```

---

## 📋 Descripción de Campos

| Campo | Tipo | Descripción |
|-------|------|-------------|
| **id** | BIGINT | Primary Key autoincremental |
| **dna_hash** | VARCHAR(64) | Hash SHA-256 del DNA (único) |
| **is_mutant** | BOOLEAN | `true` = mutante, `false` = humano |
| **created_at** | TIMESTAMP | Fecha/hora del análisis |

---

## 🔐 ¿Por qué usar Hash en lugar de guardar el DNA completo?

### ❌ Opción 1: Guardar DNA completo

```sql
dna_sequence TEXT  -- "ATGCGA,CAGTGC,TTATGT,..."
```

**Problemas:**
- ⛔ Ocupa mucho espacio (ej: 100x100 = 10KB por registro)
- ⛔ Búsquedas lentas (comparar strings largos)
- ⛔ No hay índice eficiente
- ⛔ Datos sensibles expuestos

---

### ✅ Opción 2: Guardar Hash SHA-256

```sql
dna_hash VARCHAR(64)  -- "3a5f2c9..." (siempre 64 caracteres)
```

**Ventajas:**
- ✅ **Tamaño fijo:** 64 bytes
- ✅ **Búsqueda ultra-rápida** con índice
- ✅ **Garantiza unicidad** (probabilidad de colisión: ~0)
- ✅ **Seguridad:** No se almacenan datos sensibles
- ✅ **Índice eficiente:** Hash es perfecto para indexar

---

## 🔢 Ejemplo de Hash

### Proceso de Generación

```java
String[] dna = {
    "ATGCGA",
    "CAGTGC",
    "TTATGT",
    "AGAAGG",
    "CCCCTA",
    "TCACTG"
};

// 1. Se concatena:
// "ATGCGACAGTGCTTATGTAGAAGGCCCTATCACTG"

// 2. Se aplica SHA-256:
String hash = "3a5f2c9e8b1d4f7a6c3e9d2b8f5a1c7e4d9b2f6a8c3e5d1b7f4a9c2e6d8b3f5a1";

// 3. Se guarda en BD:
// INSERT INTO dna_records (dna_hash, is_mutant, created_at)
// VALUES ('3a5f2c9e...', true, '2025-01-23 10:30:00');
```

---

## ⚡ Consultas Optimizadas

### 1. Buscar DNA analizado previamente

**Repository Method:**
```java
Optional<DnaRecord> findByDnaHash(String dnaHash);
```

**SQL Generado por Spring Data JPA:**
```sql
SELECT * FROM dna_records WHERE dna_hash = '3a5f2c9e...'
```

**Performance:** Con índice: **O(log N)** - Muy rápido ⚡

---

### 2. Contar mutantes/humanos

**Repository Method:**
```java
long countByIsMutant(boolean isMutant);
```

**SQL Generado:**
```sql
SELECT COUNT(*) FROM dna_records WHERE is_mutant = true
```

**Performance:** Con índice: **O(1)** - Instantáneo ⚡

---

### 3. Obtener estadísticas

**Repository Methods:**
```java
@Query("SELECT COUNT(d) FROM DnaRecord d WHERE d.isMutant = true")
long countMutants();

@Query("SELECT COUNT(d) FROM DnaRecord d")
long countHumans();
```

**SQL Generado:**
```sql
-- Contar mutantes
SELECT COUNT(*) FROM dna_records WHERE is_mutant = true;

-- Contar total de humanos
SELECT COUNT(*) FROM dna_records;
```

---

## 🖥️ Ver la Base de Datos (H2 Console)

### Paso 1: Iniciar la Aplicación

```bash
.\gradlew.bat bootRun
```

Espera a ver el mensaje:
```
🧬 Mutant Detection API - ONLINE
```

---

### Paso 2: Acceder a H2 Console

Abre tu navegador y ve a:
```
http://localhost:8080/h2-console
```

---

### Paso 3: Configurar la Conexión

**Formulario de conexión:**

| Campo | Valor |
|-------|-------|
| **JDBC URL** | `jdbc:h2:mem:testdb` |
| **User Name** | `sa` |
| **Password** | _(dejar vacío)_ |

Click en **"Connect"**

---

## 📊 Queries Útiles

### Ver todos los registros

```sql
SELECT * FROM dna_records;
```

**Resultado esperado:**
```
ID | DNA_HASH                                                          | IS_MUTANT | CREATED_AT
---|-------------------------------------------------------------------|-----------|--------------------
1  | 3a5f2c9e8b1d4f7a6c3e9d2b8f5a1c7e4d9b2f6a8c3e5d1b7f4a9c2e6d8b3f5a1 | true      | 2025-01-23 10:30:00
2  | 7f6e5d4c3b2a1f9e8d7c6b5a4f3e2d1c0b9a8f7e6d5c4b3a2f1e0d9c8b7a6f5e4d3 | false     | 2025-01-23 10:31:00
```

---

### Contar mutantes

```sql
SELECT COUNT(*) FROM dna_records WHERE is_mutant = true;
```

**Resultado:**
```
COUNT
-----
40
```

---

### Contar humanos (no mutantes)

```sql
SELECT COUNT(*) FROM dna_records WHERE is_mutant = false;
```

**Resultado:**
```
COUNT
-----
60
```

---

### Ver últimos 10 análisis

```sql
SELECT * FROM dna_records ORDER BY created_at DESC LIMIT 10;
```

---

### Ver DNA específico por hash

```sql
SELECT * FROM dna_records WHERE dna_hash = '3a5f2c9e...';
```

---

### Estadísticas agregadas

```sql
SELECT 
    COUNT(*) as total_records,
    SUM(CASE WHEN is_mutant = true THEN 1 ELSE 0 END) as mutants,
    SUM(CASE WHEN is_mutant = false THEN 1 ELSE 0 END) as humans,
    ROUND(
        CAST(SUM(CASE WHEN is_mutant = true THEN 1 ELSE 0 END) AS FLOAT) / 
        COUNT(*), 
        2
    ) as ratio
FROM dna_records;
```

**Resultado:**
```
TOTAL_RECORDS | MUTANTS | HUMANS | RATIO
--------------|---------|--------|------
100           | 40      | 60     | 0.40
```

---

### Ver análisis por fecha

```sql
SELECT 
    DATE(created_at) as fecha,
    COUNT(*) as total,
    SUM(CASE WHEN is_mutant = true THEN 1 ELSE 0 END) as mutantes
FROM dna_records
GROUP BY DATE(created_at)
ORDER BY fecha DESC;
```

---

### Eliminar todos los registros (útil para testing)

```sql
DELETE FROM dna_records;
```

⚠️ **Cuidado:** Esto borrará todos los datos de la tabla.

---

## 🔍 Verificar Índices

```sql
-- Ver información de los índices
SELECT * FROM INFORMATION_SCHEMA.INDEXES 
WHERE TABLE_NAME = 'DNA_RECORDS';
```

**Resultado esperado:**
```
INDEX_NAME        | COLUMN_NAME | NON_UNIQUE
------------------|-------------|------------
PRIMARY           | ID          | FALSE
IDX_DNA_HASH      | DNA_HASH    | FALSE
IDX_IS_MUTANT     | IS_MUTANT   | TRUE
```

---

## 📈 Performance de Búsquedas

| Operación | Sin Índice | Con Índice | Mejora |
|-----------|-----------|------------|--------|
| Buscar por hash | O(N) | O(log N) | **100x más rápido** |
| Contar mutantes | O(N) | O(1) | **Instantáneo** |
| Verificar duplicado | O(N) | O(log N) | **100x más rápido** |

---

## 🧪 Testing de la Base de Datos

### Script SQL para poblar datos de prueba

```sql
-- Insertar mutantes
INSERT INTO dna_records (dna_hash, is_mutant, created_at) VALUES
('a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0a1b2c3d4', true, CURRENT_TIMESTAMP),
('b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0a1b2c3d4e5', true, CURRENT_TIMESTAMP),
('c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0a1b2c3d4e5f6', true, CURRENT_TIMESTAMP);

-- Insertar humanos
INSERT INTO dna_records (dna_hash, is_mutant, created_at) VALUES
('d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0a1b2c3d4e5f6a7', false, CURRENT_TIMESTAMP),
('e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0a1b2c3d4e5f6a7b8', false, CURRENT_TIMESTAMP);
```

---

## 🛡️ Seguridad del Hash

### ¿Puede haber colisiones?

**SHA-256 genera 2^256 combinaciones posibles:**

```
2^256 = 115,792,089,237,316,195,423,570,985,008,687,907,853,269,984,665,640,564,039,457,584,007,913,129,639,936
```

**Probabilidad de colisión:**

- Para tener 50% de probabilidad de colisión, necesitarías **2^128 hashes** (~10^38)
- Si analizaras **1 millón de ADN por segundo**, tardarías **10^25 años** en tener una colisión

**Conclusión:** Prácticamente imposible ✅

---

## 🔄 Ciclo de Vida de un Registro

```
1. REQUEST → POST /mutant con DNA
              ↓
2. HASH → Calcular SHA-256 del DNA
              ↓
3. CHECK BD → SELECT * FROM dna_records WHERE dna_hash = ?
              ↓
4a. EXISTE → Retornar is_mutant guardado
              └─→ FIN (sin procesar de nuevo)
              
4b. NO EXISTE → Ejecutar algoritmo de detección
              ↓
5. SAVE → INSERT INTO dna_records (dna_hash, is_mutant, created_at)
              ↓
6. RESPONSE → Retornar resultado
```

---

## 📝 Notas Importantes

### Base de Datos en Memoria

⚠️ **Los datos se pierden al reiniciar la aplicación**

Si necesitas persistencia:

```properties
# En application.properties, cambia de:
spring.datasource.url=jdbc:h2:mem:testdb

# A persistencia en archivo:
spring.datasource.url=jdbc:h2:file:./data/testdb
```

---

### Configuración de H2 Console

En `application.properties`:

```properties
# Habilitar consola H2
spring.h2.console.enabled=true

# Ruta de acceso
spring.h2.console.path=/h2-console

# URL de la base de datos
spring.datasource.url=jdbc:h2:mem:testdb

# Credenciales
spring.datasource.username=sa
spring.datasource.password=
```

---

## 🎯 Resumen

| Aspecto | Detalle |
|---------|---------|
| **Base de datos** | H2 (en memoria) |
| **Tabla principal** | `dna_records` |
| **Campos clave** | `id`, `dna_hash`, `is_mutant`, `created_at` |
| **Índices** | `idx_dna_hash` (único), `idx_is_mutant` |
| **Hash** | SHA-256 (64 caracteres hex) |
| **Performance** | O(log N) búsqueda, O(1) conteo |
| **Console URL** | http://localhost:8080/h2-console |
| **JDBC URL** | jdbc:h2:mem:testdb |
| **Credenciales** | User: `sa`, Password: _(vacío)_ |

---

**¡Explora la base de datos usando H2 Console! 🚀**


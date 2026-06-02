import express, { Request, Response } from 'express';
import cors from 'cors';
import dotenv from 'dotenv';
import swaggerUi from 'swagger-ui-express';
import YAML from 'yamljs';
import path from 'path';
import pool from './config/database';
import UserRoutes from './routes/UserRoutes';
import GatewayRoutes from './routes/GatewayRoutes';
import SyncRoutes from './routes/SyncRoutes';
import MessageRoutes from './routes/MessageRoutes';
import MultimediaRoutes from './routes/MultimediaRoutes';



// Cargar variables de entorno desde el archivo .env
dotenv.config();

// Inicializar la aplicación Express
const app = express();
const port = process.env.PORT || 3000;

// Middleware para permitir que otros dominios nos hagan peticiones (CORS)
app.use(cors());
// Middleware para entender JSON en el cuerpo de las peticiones (Body Parser)
app.use(express.json());

// Configuración de Swagger
const swaggerDocument = YAML.load(path.join(__dirname, '../swagger/swagger.yaml'));
app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swaggerDocument));

// Ruta básica de prueba (Endpoint de salud)
app.get('/api/health', (req: Request, res: Response) => {
  res.json({
    status: 'ok',
    message: 'Servidor Red Mesh funcionando correctamente',
    timestamp: new Date().toISOString()
  });
});

// Aquí conectamos nuestro router. 
// Todas las rutas de 'userRoutes' empezarán con el prefijo '/api/usuarios'
app.use('/api/usuarios', UserRoutes);
app.use('/api/v1/users', UserRoutes);

// Todas las rutas de 'GatewayRoutes' empezarán con el prefijo '/api/gateways'
app.use('/api/gateways', GatewayRoutes);
app.use('/api/v1/gateways', GatewayRoutes);

// Todas las rutas de 'SyncRoutes' empezarán con el prefijo '/api/sync'
app.use('/api/sync', SyncRoutes);
app.use('/api/v1/sync', SyncRoutes);

// Todas las rutas de 'MessageRoutes' empezarán con el prefijo '/api/messages'
app.use('/api/messages', MessageRoutes);
app.use('/api/v1/messages', MessageRoutes);
app.use('/api/multimedia', MultimediaRoutes);
app.use('/api/v1/multimedia', MultimediaRoutes);


// Iniciar el servidor
app.listen(port, async () => {
  console.log(`🚀 Servidor corriendo en http://localhost:${port}`);

  // Prueba de conexión a la base de datos
  try {
    const res = await pool.query('SELECT NOW()');
    console.log('Base de datos conectada correctamente. Hora del servidor BD:', res.rows[0].now);
  } catch (error) {
    console.error('Error al conectar a la base de datos:', error);
  }
});

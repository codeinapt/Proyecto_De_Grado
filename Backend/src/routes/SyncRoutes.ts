import { Router } from 'express';
import { SyncController } from '../controllers/SyncController';

const router = Router();
const syncController = new SyncController();

// Manejamos la ruta POST /up que sirve para que el Gateway suba sus mensajes a la nube
router.post('/up', syncController.syncUp.bind(syncController));

// Manejamos la ruta GET /down/:gw_id para que el Gateway solicite mensajes pendientes
router.get('/down/:gw_id', syncController.syncDown.bind(syncController));

export default router;

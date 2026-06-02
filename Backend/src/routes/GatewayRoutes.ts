import { Router } from 'express';
import { GatewayController } from '../controllers/GatewayController';

const router = Router();
const gatewayController = new GatewayController();

// Manejamos el POST /heartbeat y lo conectamos a la función del controlador
router.post('/heartbeat', gatewayController.heartbeat.bind(gatewayController));

export default router;

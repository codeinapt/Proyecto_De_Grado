import { Router } from 'express';
import { MessageController } from '../controllers/MessageController';

const router = Router();
const messageController = new MessageController();

// Endpoint para confirmar la entrega de un mensaje
router.post('/ack', messageController.ack.bind(messageController));

export default router;

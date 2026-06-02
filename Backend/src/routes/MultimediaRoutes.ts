import express, { Router } from 'express';
import { MultimediaController } from '../controllers/MultimediaController';

const router = Router();
const multimediaController = new MultimediaController();

router.post(
    '/upload',
    express.raw({ type: 'application/octet-stream', limit: '20mb' }),
    multimediaController.uploadEncrypted.bind(multimediaController)
);

export default router;

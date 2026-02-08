import { ref, uploadBytes, getDownloadURL } from 'firebase/storage';
import { storage } from '@/lib/firebase';
/*
Handle uploading to Firebase
*/
export async function uploadHazardImage(file: File, userId: string): Promise<string> {
  if (!storage) throw new Error('Firebase Storage is not configured.');

  const extension = file.name.split('.').pop() || 'jpg';
  const path = `hazards/${userId}/${Date.now()}.${extension}`;
  const storageRef = ref(storage, path);

  await uploadBytes(storageRef, file);
  return getDownloadURL(storageRef);
}

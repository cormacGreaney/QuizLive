export type QuizStatus = 'DRAFT' | 'LIVE' | 'ENDED';

export type Question = {
  id: number;
  questionText: string;
  options: string[];        // now matches backend List<String>
  correctOption: number;    // index
};

export type Quiz = {
  id: number;
  title: string;
  description: string;
  status: QuizStatus;
  createdBy: number;        // owner id
  questions: Question[];
};

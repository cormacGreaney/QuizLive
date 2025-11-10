export type Question = {
  id: number;
  questionText: string;
  correctOption: number;
};

export type Quiz = {
  id: number;
  title: string;
  description: string;
  status: 'DRAFT' | 'LIVE' | 'ENDED';
  questions: Question[];
};

import Image from "next/image";


export default function Home() {
  return (
    <div className="h-screen bg-gray-900 flex flex-col items-center pt-10">
      <div className="bg-gray-800 p-10 rounded-lg shadow-lg w-full max-w-[900px]">
        <h1 className="text-white text-[40px] font-bold font-['Inter'] text-center">
          Create a new Repository
        </h1>
        
        <p className="text-white text-2xl font-bold font-['Inter'] mt-4 text-left">
          Repository name
        </p>
        <input
          type="text"
          placeholder="Enter repository name"
          className="w-full mt-2 p-3 rounded-lg bg-gray-700 text-white focus:outline-none focus:ring-2 focus:ring-blue-500"
        />

        <p className="text-white text-2xl font-bold font-['Inter'] mt-20 text-left">
          Description (optional)
        </p>
        <textarea
          placeholder="Enter a description..."
          className="w-full mt-2 p-3 rounded-lg bg-gray-700 text-white h-32 resize-none focus:outline-none focus:ring-2 focus:ring-blue-500"
        ></textarea>

        <div className="flex justify-center mt-20">  
          <button className="p-[3px] relative">
            <div className="absolute inset-0 bg-gradient-to-r from-indigo-500 to-purple-500 rounded-lg" />
            <div className="px-8 py-2 bg-gray-900 rounded-[6px] relative group transition duration-200 text-white hover:bg-transparent">
            Create Repositary
            </div>
          </button>
        </div>
      </div>
      
    </div>
  );
}

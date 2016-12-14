open Support.Pervasive
open Support.Error
open Core
open Syntax

let parseLine line =
  let lexbuf = Lexing.from_string line
  in let result =
    try Parser.toplevel Lexer.main lexbuf with Parsing.Parse_error -> 
    error (Lexer.info lexbuf) "Parse error"
in
  Parsing.clear_parser(); result

let rec process_command ctx cmd = match cmd with
  | Eval(fi,t) -> 
      let t' = eval ctx t in (* eval defined in core.mli *)
      printtm_ATerm true ctx t'; 
      Format.force_newline(); (* Forces a new line in the current box *)
      ctx
  | Bind(fi,x,bind) -> 
      let bind' = evalbinding ctx bind in (* evalbinding defined in core.mli, return a binding  *)
      pr x; pr " "; prbinding ctx bind'; Format.force_newline();
      addbinding ctx x bind' (* Adds x,bind to ctx list *)

let process_line l ctx = 
  let cmds,_ = parseLine l ctx in
  let g ctx c =  
    Format.open_hvbox 0;
    let results = process_command ctx c in
    Format.print_flush();
    results
  in
    List.fold_left g ctx cmds


let prompt = ">> " (* Prompt displayed to the user *)
let welcome_msg = 
"Welcome, this is a lambda-calculus toplevel interpreter based on Benjamin C.\n\
Pierce implementations available at https://www.cis.upenn.edu/~bcpierce/tapl/\n"

(* Write result to a file *)
(*
let string_to_file s file =
    let oc = open_out_gen [Open_creat; Open_text; Open_append] 0o640 file in
        Printf.fprintf oc "%s" (s ^ "\n");
            close_out oc *)

(*let context = ref (emptycontext)*)
let context = ref emptycontext

(* lazo is the top level interface *)
let main () =
    print_endline welcome_msg;
    try 
    begin while true do

        print_string prompt;
        let line = read_line () in 
        if String.length line = 0 then print_string "" (* Nothing got read *)
        (*else begin print_endline ("Done: " ^ line) end*) (* Returns System.context *)
        else begin 
            context := process_line line !context; () (* Returns System.context *)
        end 
    done; end
    with End_of_file -> print_endline "Goodbye!";; 

main () (* Call the main function with unit *)

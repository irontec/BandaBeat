//
//  BLAPI.m
//  Banden Lehia
//
//  Created by Iker Mendilibar on 29/10/12.
//  Copyright (c) 2012 Irontec S.L. All rights reserved.
//

#import "BLAPI.h"
#import "DSJSONRPC.h"
#import "AppDelegate.h"

@interface BLAPI()
-(void)loadLoginPreferences;
-(void)checkForErrorForMethodResult:(id)methodResult withMethodName:(NSString*)methodName withHandler:(BLAPICompletionHandler)handler;
-(void)alerViewWithText:(NSString*)error;
-(void)saveLoginPreferences;
-(void)removeLoginPreferences;
@property (strong, nonatomic) DSJSONRPC *jsonRPC;
@end


@implementation BLAPI


+(BLAPI *)sharedInstance {
    
    static  BLAPI *inst = nil;
    
    @synchronized(self){
        if (!inst) {
            inst = [[self alloc] init];
            [inst loadLoginPreferences];
      }
    }
    return inst;
}

-(id)init{
    if (!(self = [super init]))
        return self;
    
    self.jsonRPC = [[DSJSONRPC alloc] initWithServiceEndpoint:[NSURL URLWithString:@"http://m.bandabeat.com/api/json"]];   
    return self;
}
 

-(void)loginWithUsername:(NSString*)username password:(NSString*)password onCompletedHandler:(BLAPILoginHandler)handler
{
    [_jsonRPC callMethod:@"authenticate" withParameters:@[username, password] onCompletion:^(NSString *methodName, NSInteger callId, id methodResult, DSJSONRPCError *methodError, NSError *internalError) {
        if (methodError) {
            NSLog(@"\nMethod %@(%i) returned an error: %@\n\n", methodName, callId, methodError);
        }
        else if (internalError) {
            NSLog(@"\nMethod %@(%i) couldn't be sent with error: %@\n\n", methodName, callId, internalError);
        }
        else {
            
            NSLog(@"\nMethod %@(%i) completed with result: %@\n\n", methodName, callId, methodResult);
            
            NSString *error = nil;
            
            if ([methodResult isKindOfClass:NSClassFromString(@"JKArray")]) {
                error = [[methodResult lastObject] objectForKey:@"error"];
            } else {
                error = [methodResult objectForKey:@"error"];
            }

            if (error != nil) {
                handler(false, error);
            } else {                
                handler(true, methodResult);
            }
            
        }
    }];
}

-(void)getAllPlaylistWithHandler:(BLAPICompletionHandler)handler
{
    [_jsonRPC callMethod:@"getUserPlaylists" withParameters:@[self.userId, self.token] onCompletion:^(NSString *methodName, NSInteger callId, id methodResult, DSJSONRPCError *methodError, NSError *internalError) {
        if (methodError) {
            NSLog(@"\nMethod %@(%i) returned an error: %@\n\n", methodName, callId, methodError);
        }
        else if (internalError) {
            NSLog(@"\nMethod %@(%i) couldn't be sent with error: %@\n\n", methodName, callId, internalError);
        }
        else {
            [self checkForErrorForMethodResult:methodResult withMethodName:methodName withHandler:handler];
        }
    }];
}

-(void)getPublicPlaylistWithHandler:(BLAPICompletionHandler)handler
{
    [_jsonRPC callMethod:@"getPublicPlaylists" withParameters:@[self.userId, self.token] onCompletion:^(NSString *methodName, NSInteger callId, id methodResult, DSJSONRPCError *methodError, NSError *internalError) {
        if (methodError) {
            NSLog(@"\nMethod %@(%i) returned an error: %@\n\n", methodName, callId, methodError);
        }
        else if (internalError) {
            NSLog(@"\nMethod %@(%i) couldn't be sent with error: %@\n\n", methodName, callId, internalError);
        }
        else {
            [self checkForErrorForMethodResult:methodResult withMethodName:methodName withHandler:handler];
        }
    }];
}

-(void)getSongsFromPlaylist:(NSNumber*)idPlaylist withHandler:(BLAPICompletionHandler)handler
{
    [_jsonRPC callMethod:@"getPlaylistSongs" withParameters:@[idPlaylist, self.userId, self.token] onCompletion:^(NSString *methodName, NSInteger callId, id methodResult, DSJSONRPCError *methodError, NSError *internalError) {
        if (methodError) {
            NSLog(@"\nMethod %@(%i) returned an error: %@\n\n", methodName, callId, methodError);
        }
        else if (internalError) {
            NSLog(@"\nMethod %@(%i) couldn't be sent with error: %@\n\n", methodName, callId, internalError);
        }
        else {
            [self checkForErrorForMethodResult:methodResult withMethodName:methodName withHandler:handler];
        }
    }];
}

-(void)getPublicSongs:(NSNumber*)idPlaylist withHandler:(BLAPICompletionHandler)handler
{
    [_jsonRPC callMethod:@"getPlaylistSongs" withParameters:@[idPlaylist, self.userId, self.token] onCompletion:^(NSString *methodName, NSInteger callId, id methodResult, DSJSONRPCError *methodError, NSError *internalError) {
        if (methodError) {
            NSLog(@"\nMethod %@(%i) returned an error: %@\n\n", methodName, callId, methodError);
        }
        else if (internalError) {
            NSLog(@"\nMethod %@(%i) couldn't be sent with error: %@\n\n", methodName, callId, internalError);
        }
        else {
            [self checkForErrorForMethodResult:methodResult withMethodName:methodName withHandler:handler];
        }
    }];
}

-(void)checkPlaylistUpdateWithHandler:(BLAPICompletionHandler)handler
{
    [_jsonRPC callMethod:@"getGeneralToken" withParameters:@[self.userId, self.token] onCompletion:^(NSString *methodName, NSInteger callId, id methodResult, DSJSONRPCError *methodError, NSError *internalError) {
        if (methodError) {
            NSLog(@"\nMethod %@(%i) returned an error: %@\n\n", methodName, callId, methodError);
        }
        else if (internalError) {
            NSLog(@"\nMethod %@(%i) couldn't be sent with error: %@\n\n", methodName, callId, internalError);
        }
        else {
             [self checkForErrorForMethodResult:methodResult withMethodName:methodName withHandler:handler];
        }
    }];
}

-(void)checkForErrorForMethodResult:(id)methodResult withMethodName:(NSString*)methodName withHandler:(BLAPICompletionHandler)handler
{
    NSLog(@"\nMethod %@ completed with result: %@\n\n", methodName, methodResult);
    
    NSString *error = nil;
    
    if ([methodResult isKindOfClass:NSClassFromString(@"JKArray")]) {
        error = [[methodResult lastObject] objectForKey:@"error"];
    } else if([methodResult isKindOfClass:NSClassFromString(@"NSString")]) {
        error = nil;
    } else {
        error = [methodResult objectForKey:@"error"];
    }
    
    if (error != nil) {
        [self alerViewWithText:error];
    } else {
        handler(methodName, methodResult);
    }
}

-(void)alerViewWithText:(NSString*)error
{
    AppDelegate *appDelegate = (AppDelegate*) [[UIApplication sharedApplication] delegate];
    [appDelegate alerViewWithText:error];
}

-(void)logout
{
    [self removeLoginPreferences];
    AppDelegate *appDelegate = (AppDelegate*) [[UIApplication sharedApplication] delegate];
    [appDelegate logout];
}


#pragma mark - Preferences Methods

-(void)saveLoginPreferences
{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setValue:self.userId forKey:@"userId"];
    [defaults setValue:self.token forKey:@"token"];
    [defaults setValue:self.username forKey:@"username"];
    [defaults setValue:self.generalToken forKey:@"generalToken"];
    
    [defaults synchronize];
}

-(void)removeLoginPreferences
{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setValue:nil forKey:@"userId"];
    [defaults setValue:nil forKey:@"token"];
    [defaults setValue:nil forKey:@"username"];
    [defaults setValue:nil forKey:@"generalToken"];
    
    self.isLoginDataLoaded = FALSE;
    
    [defaults synchronize];

}

-(void)loadLoginPreferences
{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    
    self.userId = [defaults objectForKey:@"userId"];
    self.token = [defaults objectForKey:@"token"];
    self.username = [defaults objectForKey:@"username"];
    self.generalToken = [defaults objectForKey:@"generalToken"];
    
    if (self.token != nil) {
        self.isLoginDataLoaded = TRUE;
    }
    
}

@end

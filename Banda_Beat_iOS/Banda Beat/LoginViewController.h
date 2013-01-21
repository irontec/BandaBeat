//
//  LoginViewController.h
//  Banden Lehia
//
//  Created by Iker Mendilibar on 22/10/12.
//  Copyright (c) 2012 Irontec S.L. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "BLAPI.h"
#import "GAITrackedViewController.h"

@interface LoginViewController : GAITrackedViewController
@property(nonatomic, weak) IBOutlet UITableView *tableView;
-(IBAction)cancelLogin:(id)sender;
-(IBAction)login:(id)sender;
-(IBAction)newUser:(id)sender;
@end
